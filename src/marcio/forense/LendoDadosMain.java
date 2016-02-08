package marcio.forense;

import java.util.ArrayList;


public class LendoDadosMain {

	public static void main(String[] args) throws Exception {
		/*
		Elemento elemento = new Elemento();

		ArrayList<Elemento> ele = elemento.listarTodos();
		System.out.println("==== Listando todos:  ====");
		for (Elemento e : ele) {
			System.out.println(e.getId() + "-" + e.getNome());
		}

		System.out.println("==== Consulta por aih específico: ====");
		Elemento ele2 = elemento.listarPeloId(2);
		System.out.println(ele2.getId() + "-" + ele2.getNome());
		*/
		/*
		String texto = Long.toString(numero);  
		digito[0] = Integer.parseInt(texto.charAt(0));  // ordem inversa do exemplo anterior  
		digito[1] = Integer.parseInt(texto.charAt(1));
		*/  
		String texto = "91.284,90";	texto = texto.replace(".","");	texto = texto.replace(",", "");
		int valor = Integer.parseInt(texto);
		int[] digito = new int[texto.length()];
		for (int i=0;i<texto.length();i++){
			digito[i] = Integer.parseInt(String.valueOf(texto.charAt(i)));
		}
		for (int dig:digito){
			System.out.println(dig);
		}
	}
}
