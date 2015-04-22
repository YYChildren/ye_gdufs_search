package commtest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ATest {
	@Test
	public void testLib(){
		System.out.println(System.getProperty("java.library.path"));
	}
	@Test
	public void  testSubList(){
		List<Integer> il = new ArrayList<>();
		il.add(1);
		il.add(2);
		il.add(3);
		il.add(4);
		il.add(5);
		il.add(6);
		il.add(7);
		System.out.println(il.subList(6, 7));
	}
}
