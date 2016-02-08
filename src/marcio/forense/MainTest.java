package marcio.forense;

import java.util.ArrayList;

public class MainTest {

	public static void main(String[] args) {
		EntidadeDAO ent = new EntidadeDAO();
		ent = ent.getEntidadeByName("","Semad");
		System.out.println(ent.getId());
		System.out.println(ent.getNome());
		}
	}
