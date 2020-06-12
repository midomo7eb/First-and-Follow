import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CFG {

    private String startingVariable;
    private Set<String> variables;
    private Set<String> terminals;
    private Map<String, ArrayList<String>> rules;
    private Map<String, Set<String>> firstFinal;
    
    public CFG(String input) {
    	variables = new HashSet<>();
    	terminals = new HashSet<>();
    	rules = new HashMap<>();
    	this.startingVariable = input.charAt(0)+"";
    	String[] splitted = input.split(";");
    	for(String rules:splitted) {
    		for(int i = 2;i<rules.length();i++) {
    			if(Character.isLowerCase(rules.charAt(i))) {
    				if(rules.charAt(i)=='e') {
    					this.terminals.add("$");
    				}
    				else {
    					this.terminals.add(rules.charAt(i)+"");
    				}
    			}
    		}
    		this.variables.add(rules.charAt(0)+"");
    		String[] rule = rules.split(",");
    		ArrayList<String> list = new ArrayList<>();
    		for(int i = 1;i<rule.length;i++) {
    			if(rule[i].contains("e")) {
    				list.add("$");
    			}
    			else {
        			list.add(rule[i]);
    			}
    		}
    		this.rules.put(rule[0], list);
    	}
    }
    public CFG(String startingVariable, Set<String> variables, Set<String> terminals, Map<String, ArrayList<String>> rules) {
        this.startingVariable = startingVariable;
        this.variables = variables;
        this.terminals = terminals;
        this.rules = rules;
    }
    public static final String EPSILON = "$";

    private boolean isSubset(Set<String> setA, Set<String> setB) {
        if (setA == null || setB == null) {
            return false;
        }
        boolean aHasEps = false;
        boolean bHasEps = false;
        boolean returnValue;
        if (!setA.isEmpty() && setA.contains(EPSILON)) {
            aHasEps = true;
        } else {
            setA.add(EPSILON);
        }
        if (!setB.isEmpty() && setB.contains(EPSILON)) {
        	bHasEps = true;
        } else {
            setB.add(EPSILON);
        }
        if (setB.containsAll(setA)) {
            returnValue = true;
        } else
            returnValue = false;
        if (!aHasEps) {
            setA.remove(EPSILON);
        }
        if (!bHasEps) {
            setB.remove(EPSILON);
        }
        return returnValue;
    }

    private String[] parser(String production) {
        return production.split("");
    }

    public  String First() {
        Map<String, Set<String>> first = new LinkedHashMap<>();
        for (String t : this.terminals) {
            first.put(t, new HashSet<>(Collections.singletonList(t)));
        }
        for (String v : this.variables) {
            first.put(v, new HashSet<>(Collections.emptyList()));
        }

        boolean change = true;
        String[] productionArray;

        while (change) {
            change = false;
            for (Map.Entry<String, ArrayList<String>> entry : this.rules.entrySet()) {
                for (String production : entry.getValue()) {
                    productionArray = parser(production);
                    if (first.get(productionArray[0]).contains(EPSILON)) {
                        if (!first.get(entry.getKey()).contains(EPSILON)) {                        	
                            first.get(entry.getKey()).add(EPSILON);
                            change = true;
                        }
                    }
                    // To handle rules containing left Recursion
                    if (productionArray[0].equals(entry.getKey())) {
                        for (int i = 0; i < productionArray.length - 1; i++) {
                            if (first.get(productionArray[i]).contains(EPSILON)) {
                                first.get(entry.getKey()).addAll(first.get(productionArray[i + 1]));
                            }
                        }
                    }
                    if (!first.get(productionArray[0]).isEmpty() && !isSubset(first.get(productionArray[0]), first.get(entry.getKey()))) {
                    	first.get(entry.getKey()).addAll(first.get(productionArray[0]));
                    	if(first.get(productionArray[0]).contains(EPSILON)) {
                    		if(productionArray.length>1) {
                        		first.get(entry.getKey()).addAll(first.get(productionArray[1]));
                    		}
                    	}
                        change = true;
                    }
                   

                }
            }
        }
        firstFinal =  new LinkedHashMap<>(first);
        return encodeFirst(first);
    }
    public  String Follow() {
    	 Map<String, Set<String>> first = new LinkedHashMap<>(firstFinal);
         Map<String, Set<String>> follow = new LinkedHashMap<>();
         for (String v : this.variables) {
             if (v.equals(this.startingVariable)) {
                 follow.put(v, new HashSet<>(Collections.singleton(EPSILON)));
             } else
                 follow.put(v, new HashSet<>(Collections.emptyList()));
         }
         for (String t : this.terminals) {
             follow.put(t, new HashSet<>(Collections.emptyList()));
         }
         boolean change = true;
         while(change) {
        	 change = false;
        	 String[] productionArray;
        	 for (Map.Entry<String, ArrayList<String>> entry : this.rules.entrySet()) {
        	     for (String production : entry.getValue()) {
                     productionArray = parser(production);
                     if (this.variables.contains(productionArray[productionArray.length - 1])) {
                         follow.get(productionArray[productionArray.length - 1]).addAll(follow.get(entry.getKey()));
                     }
                     if (productionArray.length >= 2) {
                         for(int i = 0;i<productionArray.length-1;i++) {
                        	 if (!isSubset(first.get(productionArray[i+1]), follow.get(productionArray[i]))) {
                                 follow.get(productionArray[i]).addAll(first.get(productionArray[i + 1]));
                                 change = true;
                             }
                            
                         }
                         for(int i = 0;i<productionArray.length-1;i++) {
                             if (first.get(productionArray[i + 1]).contains(EPSILON)) {
	                        	 if (!isSubset(follow.get(entry.getKey()), follow.get(productionArray[i]))) {
	                                 follow.get(productionArray[i]).addAll(follow.get(entry.getKey()));
	                                 change = true;
	
	                             }
	                        	if(i==0&&i+2<productionArray.length) {
	                        		 follow.get(productionArray[i]).addAll(first.get(productionArray[i + 2]));
	                        	}
                             }
                             
                         }
                        
                     }
                     else {
                         follow.get(productionArray[0]).addAll(follow.get(entry.getKey()));
                     }
                   
        	     }
        	 }
         }
         return encodeFollow(follow);
    }
    public  String encodeFirst(Map<String, Set<String>> first) {
        String overall = "";
        boolean startVar = false;
        String start = "";
        for (String v : this.variables) {
        	if(v.equals("S")) {
        		startVar = true;
        	}
        	String part = "";
        	Object[] firstString= first.get(v).toArray();
        	for(int i = 0 ;i<firstString.length;i++) {
        		if(firstString[i].equals("$")) {
        			part+="e";
//        			System.out.print("e");
        		}
        		else {
        			part+=firstString[i];
//            		System.out.print(firstString[i]);
        		}
        	}
            char[] charArray = part.toCharArray();
            //4
            for (int i = 0; i < charArray.length; i++) {
                for (int j = i + 1; j < charArray.length; j++) {
                    if (Character.toLowerCase(charArray[j]) < Character.toLowerCase(charArray[i])) {
                        swapChars(i, j, charArray);
                    }
                }
            }
            part = String.valueOf(charArray);
        	part = v+","+part+";";
        	if(!startVar) {
            	overall+=part;
        	}
        	else {
        		start+=part;
        	}
        
            
        }
        overall = start + overall;
    	return overall;

    }
    private static void swapChars(int i, int j, char[] charArray) {
        char temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;
    }
    public  String encodeFollow(Map<String, Set<String>> follow) {
    	
        String overall = "";
        boolean startVar = false;
        String start = "";
        for (String v : this.variables) {
        	if(v.equals("S")) {
        		startVar = true;
        	}
      	  boolean eps = false;
        	String part = "";
        	Object[] firstString= follow.get(v).toArray();
        	for(int i = 0 ;i<firstString.length;i++) {
        		if(firstString[i].equals("$")) {
        			eps = true;
//        			System.out.print("e");
        		}
        		else {
        			part+=firstString[i];
//            		System.out.print(firstString[i]);
        		}
        	}
            char[] charArray = part.toCharArray();
            //4
            for (int i = 0; i < charArray.length; i++) {
                for (int j = i + 1; j < charArray.length; j++) {
                    if (Character.toLowerCase(charArray[j]) < Character.toLowerCase(charArray[i])) {
                        swapChars(i, j, charArray);
                    }
                }
            }
            part = String.valueOf(charArray);
            if(!startVar) {
            	 if(eps) {
                  	 part = v+","+part+"$"+";";
                    }
                    else {
                      part = v+","+part+";";
                    }
                    overall+=part;
            }
            else {
            	 if(eps) {
                  	 part = v+","+part+"$"+";";
                    }
                    else {
                      part = v+","+part+";";
                    }
                    start+=part;
            }
           
        
            
        }
        overall = start + overall;
    	return overall;

  }
}
