package mondrian.fmdql.servlet;


public interface IRenderFMDQL {
	
	public StringBuffer renderResultado(StringBuffer html);
	
	public void renderCabecalhoDimensaoColuna(StringBuffer html, int coluna);
	
	public int quantidadeDeColunasExtras();

}
