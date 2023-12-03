package PatternRecognition;

import java.util.HashMap;
import java.util.Vector;

public class DSU {
    public HashMap<Integer, Vector<String>> sets;

    public DSU() {
        sets = new HashMap<>();
    }

    public void Add(String node) {
        int lastSetId = sets.size();
        var vect = new Vector<String>();
        vect.add(node);
        sets.put(lastSetId, vect);
    }

    public void Union(String n1, String n2) {
        var s1 = Find(n1);
        var s2 = Find(n2);
        if (s1 < 0) {
            System.out.println("Error: cannot find node " + n1);
            return;
        }
        if (s2 < 0) {
            System.out.println("Error: cannot find node " + n2);
            return;
        }
        if (s1 == s2)
            return;
        var s1Values = sets.get(s1);
        var s2Values = sets.get(s2);
        if (s1Values.size() >= s2Values.size()) {
            s1Values.addAll(s2Values);
            sets.remove(s2);
        } else {
            s2Values.addAll(s1Values);
            sets.remove(s1);
        }
    }

    public Integer Find(String node) {
        for (var key : sets.keySet()) {
            if (sets.get(key).contains(node))
                return key;
        }
        return -1;
    }
}
