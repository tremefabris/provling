package br.ufscar.dc.compiladores.provling;


import java.util.LinkedList;
import java.util.List;

import br.ufscar.dc.compiladores.provling.SymbolTable;


public class Scopes {
    
    private LinkedList<SymbolTable> tableStack;

    public Scopes() {
        tableStack = new LinkedList<>();
        createNewScope();
    }

    public void createNewScope() {
        tableStack.push(new SymbolTable());
    }

    public SymbolTable currentScope() {
        return tableStack.peek();
    }

    public List<SymbolTable> browseNestedScopes() {
        return tableStack;
    }

    public void abandonScope() {
        tableStack.pop();
    }

    /*
     * VERIFY EXISTENCE BY ITERATING
     * OVER SYMBOL TABLES
     */
    public boolean exists(String var_name) {
        for (SymbolTable st: tableStack)
            if (st.exists(var_name))
                return true;

        return false;
    }

    /*
     * VERIFY TYPE BY ITERATING OVER
     * SYMBOL TABLES
     */
    

    

    /*
     * GET SYMBOL TABLE WHERE A CERTAIN
     * VARIABLE IS STORED
     */
    public SymbolTable getTableFrom(String var_name) {
        for (SymbolTable st: tableStack)
            if (st.exists(var_name))
                return st;

        return null;                
    }

}