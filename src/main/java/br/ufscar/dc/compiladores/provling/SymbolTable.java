package br.ufscar.dc.compiladores.provling;

import java.util.HashMap;
import java.util.Map;
public class SymbolTable {
    
 class EntrySymbolTable {
        String name;
        String frase;

        private EntrySymbolTable(String name, String frase) {
            this.name = name;
            this.frase = frase;
        }
    }

    /*
     * Declarations for SymbolTable
     */
    private final Map<String, EntrySymbolTable> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public void add(String name, String frase) {
        table.put(name, new EntrySymbolTable(name, frase));
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public boolean existsequal(String frase){
        return table.containsValue(frase);
    }

    
}

