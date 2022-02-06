package mongodb;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class ShelledConnection {
    private LinkedBlockingQueue<String> commandsQueue = new LinkedBlockingQueue<>();

    private Process shellProcess;

    private Reader stdout;
    private Reader stderr;
    private Writer stdin;

    // blocks threads each time terminal is busy and not ready for input. For example, whet it's trying to connect to db
    private CountDownLatch countDownLatchReadiness = new CountDownLatch(1);

    public ShelledConnection(String pathToShell) {
        new Thread(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder(pathToShell);
            processBuilder.redirectErrorStream(true);
            try {
                shellProcess = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setStdout(new BufferedReader(new InputStreamReader(shellProcess.getInputStream())));
            setStderr(new BufferedReader(new InputStreamReader(shellProcess.getErrorStream())));
            setStdin(new BufferedWriter(new OutputStreamWriter(shellProcess.getOutputStream())));
            try {
                // our stds aka streams are set and ready, same with the process.
                // So we're ready to unlock all threads that depend on this latch.
                countDownLatchReadiness.countDown();
                // keep this thread alive since we still need the streams above (as long as process alive)
                shellProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void input(String input) {
        commandsQueue.offer(input);
        new Thread(() -> {
            // this shouldn't happen, but just in case...
            if (commandsQueue.isEmpty()) {
                return;
            }
            String inputForStdin = commandsQueue.poll();
            try {
                getStdin().write(inputForStdin);
                getStdin().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void blockThreadUntilReadiness() {
        if (countDownLatchReadiness.getCount() <= 0) {
            // i'm not sure but we might need to throw an exception here...
            return;
        }
        try {
            countDownLatchReadiness.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Reader getStdout() {
        return stdout;
    }

    public void setStdout(Reader stdout) {
        this.stdout = stdout;
    }

    public Reader getStderr() {
        return stderr;
    }

    public void setStderr(Reader stderr) {
        this.stderr = stderr;
    }

    public Writer getStdin() {
        return stdin;
    }

    public void setStdin(Writer stdin) {
        this.stdin = stdin;
    }
}
