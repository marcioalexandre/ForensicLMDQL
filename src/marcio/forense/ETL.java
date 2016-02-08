package marcio.forense;
import java.util.ArrayList;

import marcio.forense.*;

public class ETL {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Xlink3 x = new Xlink3();
		ArrayList<String> arrEle 	= x.getElements("fb-20131231.xml"); 
		ArrayList<String> iele 	= x.getInstanceElements("fb-20131231.xml", "2013", "Q4", arrEle);

	}

}
