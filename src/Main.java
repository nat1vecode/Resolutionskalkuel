
import java.util.*;

public class Main {

    public static List<String> steps = new ArrayList<>();
    public static List<Integer> isteps = new ArrayList<>();
    public static List<Integer> ksteps = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<List<String>> clauses;
        String input = sc.nextLine();
        input = input.replaceAll("\\s", "");
        clauses = constructClausesList(input);
        int clausesSize = clauses.size();
        ksteps = new ArrayList<>(clausesSize);
        isteps = new ArrayList<>(clausesSize);
        clauses = padClausesList(clauses);
        for (List<String> ls : clauses) {
            ls.forEach(System.out::print);
            System.out.println();
        }
        clauses = resolution(clauses);
        printClauses(unpadList(clauses), clausesSize);
    }

    public static void printClauses(List<List<String>> l, int clausesSize) {
        int stepsC = 0;
        for (int i = 0; i < l.size(); i++) {
            System.out.print(i + 1 + ". {");
            if (l.get(i).isEmpty()) {
                System.out.print("}" + steps.get(stepsC));
                break;
            }
            for (int j = 0; j < l.get(i).size(); j++) {
                String s = l.get(i).get(j).replaceAll("¬", "!");
                s = s.replaceAll("-", "");
                if (j == l.get(i).size() - 1) {
                    if (i >= clausesSize) {
                        System.out.print(s + "}");
                        System.out.println(steps.get(stepsC++));
                    } else {
                        System.out.println(s + "}");
                    }
                } else {
                    if (s.equals("") || s.equals(" ")) continue;
                    System.out.print(s + ", ");
                }
            }
        }
    }

    public static List<String> convertToCharacterList(String s) {
        int l = 1;
        List<String> a = new ArrayList<>();
        s = s.replaceAll("\uD835\uDC4E", "a");
        s = s.replaceAll("\uD835\uDC4F", "b");
        s = s.replaceAll("\uD835\uDC50", "c");
        s = s.replaceAll("\uD835\uDC51", "d");
        if (!s.contains("(")) {
            a.add(s);
            return a;
        }
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '∨') {
                a.add(s.substring(l, i));
                l = i + 1;
            } else if (i == s.length() - 2) {
                a.add(s.substring(l, i + 1));
            }
        }
        return a;
    }

    public static List<List<String>> constructClausesList(String s) {
        int l = 0;
        List<List<String>> a = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '∧') {
                a.add(convertToCharacterList(s.substring(l, i)));
                l = i + 1;
            } else if (i == s.length() - 2) {
                a.add(convertToCharacterList(s.substring(l, i + 2)));
            }
        }
        return a;
    }

    public static List<List<String>> padClausesList(List<List<String>> toPad) {
        List<List<String>> padded = new ArrayList<>();
        int n = 0;
        int i = 0;
        HashMap<String, Integer> keys = new HashMap<>();
        List<String> biggestList = new ArrayList<>(toPad.get(0));
        for (List<String> clauses : toPad) {
            if (clauses.size() > n) {
                biggestList = new ArrayList<>(clauses);
                n = clauses.size();
            }
        }
        for (String s : biggestList) {
            s = s.replaceAll("¬", "");
            keys.put(s, i++);
        }
        for (int j = 0; j < toPad.size(); j++) {
            if (toPad.get(j).size() == biggestList.size()) {
                padded.add(toPad.get(j));
                continue;
            }
            List<String> temp = new ArrayList<>(biggestList);
            for (int l = 0; l < temp.size(); l++) {
                temp.set(l, "-");
            }
            for (int k = 0; k < toPad.get(j).size(); k++) {
                temp.set(keys.get(toPad.get(j).get(k).replaceAll("¬", "")), toPad.get(j).get(k));
            }
            padded.add(temp);
        }
        return padded;
    }

    public static List<List<String>> unpadList(List<List<String>> toUnpad) {
        for (List<String> clause : toUnpad) {
            clause.removeIf(s -> s.equals("-"));
            for (String s : clause) {
                s = s.replaceAll("\uD835\uDC4E", "a");
            }
        }
        return toUnpad;
    }

    public static List<List<String>> resolution(List<List<String>> clauses) {
        boolean active = true;
        while (active) {
            for (int i = 0; i < clauses.size(); i++) {
                int listSize = clauses.size();
                for (int k = i + 1; k < listSize; k++) {
                    for (int j = 0; j < clauses.get(i).size(); j++) {
                        for (int l = 0; l < clauses.get(k).size(); l++) {
                            if (clauses.get(i).get(j).equals(clauses.get(k).get(l))) continue;
                            if (clauses.get(i).get(j).length() == clauses.get(k).get(l).length()) continue;
                            if (!clauses.get(i).get(j).replaceAll("¬", "").equals(clauses.get(k).get(l).replaceAll("¬", "")))
                                continue;
                            List<String> res = new ArrayList<>(clauses.get(i));
                            List<String> res2 = new ArrayList<>(clauses.get(k));

                            res.set(clauses.get(i).indexOf(clauses.get(i).get(j)), "-");
                            res2.set(clauses.get(k).indexOf(clauses.get(k).get(j)), "-");

                            int counti = 0;
                            int countk = 0;
                            boolean lastCheck = false;
                            for (int m = 0; m < clauses.get(i).size(); m++) {
                                if (res.get(m).length() != res2.get(m).length()) lastCheck = true;
                                if (!res.get(m).equals(res2.get(m))) {
                                    if (res.get(m).equals("-")) {
                                        res.set(m, res2.get(m));
                                    }
                                }
                                if (res.get(m).equals("-")) counti++;
                                if (res2.get(m).equals("-")) countk++;
                            }
                            if (lastCheck) continue;

                            isteps.add(i + 1);
                            ksteps.add(k + 1);
                            steps.add(" aus " + (i + 1) + " und " + (k + 1));
                            clauses.add(res);
                            System.out.println("Tick...");
                            if ((counti == countk) && counti == res.size()) return clauses;
                        }
                    }
                }
            }
        }
        return clauses;
    }

    public static List<List<String>> tidyUp(List<List<String>> clauses) {
        Set<Integer> usedIndices = new HashSet<>();
        List<List<String>> result = new ArrayList<>();

        // Letzte Klausel ist die leere Klausel (Widerspruch)
        int currentIndex = clauses.size() - 1;

        // Rückverfolgen ab der letzten Klausel
        traceBack(currentIndex, usedIndices);

        // Sammle nur die genutzten Klauseln
        for (int i = 0; i < clauses.size(); i++) {
            if (usedIndices.contains(i)) {
                result.add(clauses.get(i));
            }
        }

        // Schritte entsprechend neu berechnen (optional, wenn du sie neu brauchst)
        List<String> newSteps = new ArrayList<>();
        for (int i = 0; i < isteps.size(); i++) {
            if (usedIndices.contains(clauses.size() - isteps.size() + i)) {
                int left = isteps.get(i);
                int right = ksteps.get(i);
                newSteps.add(" aus " + (left + 1) + " und " + (right + 1));
            }
        }
        steps = newSteps;

        return result;
    }

    private static void traceBack(int index, Set<Integer> usedIndices) {
        // Bereits genutzt? Dann abbrechen
        if (usedIndices.contains(index)) return;

        usedIndices.add(index);

        // Wenn Index kleiner als ursprüngliche Eingabeanzahl: keine weiteren Schritte
        if (index < isteps.size()) return;

        int derivedIndex = index - isteps.size();
        if (derivedIndex >= isteps.size()) return; // Sicherheit

        int i = isteps.get(derivedIndex);
        int k = ksteps.get(derivedIndex);

        traceBack(i, usedIndices);
        traceBack(k, usedIndices);
    }
}