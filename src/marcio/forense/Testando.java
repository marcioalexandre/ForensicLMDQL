package marcio.forense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Testando {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> a = new ArrayList<String>();
		a.add("Marcio");
		a.add(null);
		a.add("Clara");
		a.add(null);
		a.add("binho");
		a.removeAll(Collections.singleton(null));
		
		for (String a1:a){
			System.out.println(a1);
		}

	}

}
