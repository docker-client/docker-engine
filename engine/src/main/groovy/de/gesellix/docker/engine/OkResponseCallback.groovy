package de.gesellix.docker.engine

import de.gesellix.util.IOUtils
import groovy.util.logging.Slf4j
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Okio

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class OkResponseCallback implements Callback {

    OkHttpClient client
    ConnectionProvider connectionProvider
    AttachConfig attachConfig
    Closure onResponse
    Closure onSinkClosed
    Closure onSourceConsumed

    OkResponseCallback(OkHttpClient client, ConnectionProvider connectionProvider, AttachConfig attachConfig) {
        this.client = client
        this.connectionProvider = connectionProvider
        this.attachConfig = attachConfig
        this.onResponse = attachConfig.onResponse
        this.onSinkClosed = attachConfig.onSinkClosed
        this.onSourceConsumed = attachConfig.onSourceConsumed
    }

    @Override
    void onFailure(Call call, IOException e) {
        log.error("connection failed: ${e.message}", e)
        attachConfig.onFailure(e)
    }

    void onFailure(Exception e) {
        log.error("error", e)
        attachConfig.onFailure(e)
    }

    @Override
    void onResponse(Call call, Response response) throws IOException {
        TcpUpgradeVerificator.ensureTcpUpgrade(response)

        if (attachConfig.streams.stdin != null) {
            // pass input from the client via stdin and pass it to the output stream
            // running it in an own thread allows the client to gain back control
            def stdinSource = Okio.source(attachConfig.streams.stdin)
            Thread writer = new Thread(new Runnable() {

                @Override
                void run() {
                    try {
                        def bufferedSink = Okio.buffer(connectionProvider.sink)
                        IOUtils.copy(stdinSource, bufferedSink.getBuffer())
                        bufferedSink.flush()
                        def done = new CountDownLatch(1)
                        delayed(100, {
                            try {
                                bufferedSink.close()
                                onSinkClosed(response)
                            }
                            catch (Exception e) {
                                log.warn("error", e)
                            }
                        }, done)
                        done.await(5, SECONDS)
                    }
                    catch (InterruptedException e) {
                        log.debug("stdin->sink interrupted", e)
                        Thread.currentThread().interrupt()
                    }
                    catch (Exception e) {
                        onFailure(e)
                    }
                    finally {
                        log.trace("writer finished")
                    }
                }
            })
            writer.setName("stdin-writer ${call.request().url().encodedPath()}")
            writer.start()
        }
        else {
            log.debug("no stdin.")
        }

        if (attachConfig.streams.stdout != null) {
            def bufferedStdout = Okio.buffer(Okio.sink(attachConfig.streams.stdout))
            Thread reader = new Thread(new Runnable() {

                @Override
                void run() {
                    try {
                        IOUtils.copy(connectionProvider.source, bufferedStdout.getBuffer())
                        bufferedStdout.flush()
                        def done = new CountDownLatch(1)
                        delayed(100, {
                            onSourceConsumed()
                        }, done)
                        done.await(5, SECONDS)
                    }
                    catch (InterruptedException e) {
                        log.debug("source->stdout interrupted", e)
                        Thread.currentThread().interrupt()
                    }
                    catch (Exception e) {
                        onFailure(e)
                    }
                    finally {
                        log.trace("reader finished")
                    }
                }
            })
            reader.setName("stdout-reader ${call.request().url().encodedPath()}")
            reader.start()
        }
        else {
            log.debug("no stdout.")
        }

        onResponse(response)
    }

    static delayed(long delay, Closure action, CountDownLatch done) {
        new Timer(true).schedule(
                new TimerTask() {

                    @Override
                    void run() {
                        Thread.currentThread().setName("Delayed action (${Thread.currentThread().getName()})")
                        try {
                            action()
                        }
                        finally {
                            done.countDown()
                            cancel()
                        }
                    }
                },
                delay
        )
    }
}
