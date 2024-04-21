import java.io.*;
import java.nio.file.*;
import java.util.concurrent.SynchronousQueue;

public class Main {
    private static final SynchronousQueue<Integer> queueToConsumer1 = new SynchronousQueue<>();
    private static final SynchronousQueue<Integer> queueToConsumer2 = new SynchronousQueue<>();

    public static void main(String[] args) throws InterruptedException {
        Thread source = new Thread(() -> {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get("source.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int number = Integer.parseInt(line);
                    queueToConsumer1.put(number);
                    queueToConsumer2.put(number);
                }
                queueToConsumer1.put(0);
                queueToConsumer2.put(0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer1 = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("consumer1.txt"))) {
                while (true) {
                    int number = queueToConsumer1.take();
                    if (number == 0) break;
                    writer.write(Integer.toString(number * number) + "\n");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer2 = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("consumer2.txt"))) {
                while (true) {
                    int number = queueToConsumer2.take();
                    if (number == 0) break;
                    writer.write(Integer.toString(number * number * number) + "\n");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        source.start();
        consumer1.start();
        consumer2.start();

        source.join();
        consumer1.join();
        consumer2.join();
    }
}