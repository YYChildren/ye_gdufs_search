
package commtest;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorTest {
	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(3); 
		executor.execute(new Run(1));
		executor.execute(new Run(2));
		executor.execute(new Run(3));
		executor.execute(new Run(4));
		executor.execute(new Run(5));
		System.out.println(executor.getActiveCount());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(executor.getActiveCount());
	}
}
class Run implements Runnable{
	int n;
	public Run(int n){
		this.n = n;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
			System.out.println("aaa"+n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
