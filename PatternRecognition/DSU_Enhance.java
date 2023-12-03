package PatternRecognition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class DSU_Enhance {
    public HashMap<String, String> sets;
    private HashMap<String, Integer> rank;
    private HashSet<String> roots;

    public DSU_Enhance() {
        sets = new HashMap<>();
        rank = new HashMap<>();
        roots = new HashSet<>();
    }

    public void Add(String node) {
        sets.put(node, node);
        rank.put(node, 1);
        roots.add(node);
    }

    public void Union(String n1, String n2) {
        var s1 = Find(n1);
        var s2 = Find(n2);
        if (s1 == s2)
            return;
        var r1 = rank.get(s1);
        var r2 = rank.get(s2);
        if (r1 >= r2) {
            sets.put(s2, s1);
            rank.put(s1, r1 + r2);
            roots.remove(s2);
        } else {
            sets.put(s1, s2);
            rank.put(s2, r1 + r2);
            roots.remove(s1);
        }
    }

    public String Find(String node) {
        var cur = sets.get(node);
        if (cur != sets.get(cur))
            cur = sets.get(cur);
        return cur;
    }

    public HashMap<String, Vector<String>> GetSets(){
        var result = new HashMap<String, Vector<String>>();
        for(var root:roots)
            result.put(root, new Vector<String>());
        for(var key: sets.keySet()){
            result.get(Find(key)).add(key);
        }
        return result;
    }
}
