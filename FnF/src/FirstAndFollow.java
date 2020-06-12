import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
public class FirstAndFollow {

 
	//T11_37_4158_Ahmed_Hossam
    public static void main(String[] args) {
    	//Marwa
//    	String input  = "S,ABCDZ;A,a,e;B,b,e;C,c;D,d,e;Z,z,e";
//    	String input  = "S,TX;X,aTX,e;T,FV;V,sFV,e;F,zSx,i";
//    	String input  = "S,ACB,CbB,Ba;A,da,BC;B,g,e;C,h,e";
//    	String input  = "S,Bb,Cd;B,aB,e;C,cC,e";
//    	String input  = "S,aBDh;B,cC;C,bC,e;D,EF;E,g,e;F,f,e";
//    	String input  = "S,A;A,aBF;F,dF,e;B,b;C,g";
//    	String input  = "S,zLx,a;L,SQ;Q,qSQ,e";
//    	String input  = "S,AaAb,BbBa;A,e;B,e";
//    	String input  = "S,TQ;Q,aTQ,e;T,FW;W,xFW,e;F,qSw,i";
//    	String input  = "S,ACB,CbB,Ba;A,da,BC;B,g,e;C,h,e";
    	//Morgan
    	String input  = "S,ScT,T;T,aSb,iaLb,e;L,SdL,S";
//    	String input  = "S,aTbS,e;T,aTb,e";
//    	String input  = "S,SAB,SBC,e;A,aAa,e;B,bB,e;C,cC,e";
//    	String input  = "S,AB;A,aA,b;B,CA;C,cC,d";
//    	String input  = "S,lAr,a;A,lArB,aB;B,cSB,e";
//    	String input  = "S,aA;A,SB,e;B,bA,cA";
    	CFG cfg = new CFG(input);
    	String firstEncoding = cfg.First();
    	String followEncoding = cfg.Follow();
    	System.out.println("First: "+firstEncoding);
    	System.out.println("Follow: "+followEncoding);
 
    }
    public static class CFG {

        private String startingVariable;
        private Set<String> variables;
        private Set<String> terminals;
        private Map<String, ArrayList<String>> rules;
        private Map<String, Set<String>> firstFinal;
        
        public CFG(String input) {
        	variables = new HashSet<>();
        	terminals = new HashSet<>();
        	rules = new LinkedHashMap<>();
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
                        boolean isEpsilon=true;
                        int index = 0;
                        for(int i = 0;i<productionArray.length;i++) {
                        	if(!first.get(productionArray[i]).contains(EPSILON)) {
                        		isEpsilon = false;
                        		index = i;
                        		break;
                        	}
                        }
                        if(isEpsilon) {
                        	if(!first.get(entry.getKey()).contains(EPSILON)) {
                        		first.get(entry.getKey()).add(EPSILON);
                        		change = true;
                        	}
                        }
                      
                        if (productionArray[0].equals(entry.getKey())) {
                            for (int i = 0; i < productionArray.length - 1; i++) {
                                if (first.get(productionArray[i]).contains(EPSILON)) {
                                    first.get(entry.getKey()).addAll(first.get(productionArray[i + 1]));
                                }
                            }
                        }
                        if(productionArray.length>1) {
                        	if (first.get(productionArray[0]).contains(EPSILON)) {
                        		Set<String> xFX = new HashSet<String>(first.get(productionArray[1]));
                        		xFX.remove("$");
                        		Map<String, Set<String>> tempWithNoEps = new LinkedHashMap<>();
                                tempWithNoEps.put(productionArray[1], new HashSet<>(Collections.emptyList()));
                                tempWithNoEps.get(productionArray[1]).addAll(xFX);
                        		first.get(entry.getKey()).addAll(tempWithNoEps.get(productionArray[1]));
                        	}
                        }
                           
                        
             	  		Set<String> xFX = new HashSet<String>(first.get(productionArray[0]));
                		xFX.remove("$");
                		Map<String, Set<String>> tempWithNoEps = new LinkedHashMap<>();
                        tempWithNoEps.put(productionArray[0], new HashSet<>(Collections.emptyList()));
                        tempWithNoEps.get(productionArray[0]).addAll(xFX);
                        if (!tempWithNoEps.get(productionArray[0]).isEmpty() && !isSubset(tempWithNoEps.get(productionArray[0]), first.get(entry.getKey()))) {
                        	first.get(entry.getKey()).addAll(tempWithNoEps.get(productionArray[0]));
                        	if(first.get(productionArray[0]).contains(EPSILON)) {
                        		
                        		for(int i = 1;i<productionArray.length;i++) {
                        	  		Set<String> xFY= new HashSet<String>(first.get(productionArray[i]));
                            		xFY.remove("$");
                            		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
                                    tempWithNoEps2.put(productionArray[i], new HashSet<>(Collections.emptyList()));
                                    tempWithNoEps2.get(productionArray[i]).addAll(xFY);
                        			if(first.get(productionArray[i]).contains(EPSILON)) {
                        				first.get(entry.getKey()).addAll(tempWithNoEps2.get(productionArray[i]));
                        			}
                        			else {
                        				first.get(entry.getKey()).addAll(tempWithNoEps2.get(productionArray[i]));
                        				break;
                        			}
                        			
                        		}
//                        		if(productionArray.length>1) {
//                            		first.get(entry.getKey()).addAll(first.get(productionArray[1]));
//                        		}
                        	}
                            change = true;
                        }
//                        for(int i = 0;i<productionArray.length;i++) {
//                        	if(i==0||isEpsilon) {
//                        		Set<String> xFX = new HashSet<String>(first.get(productionArray[i]));
//                        		xFX.remove("$");
//                        		Map<String, Set<String>> tempWithNoEps = new LinkedHashMap<>();
//                                tempWithNoEps.put(productionArray[i], new HashSet<>(Collections.emptyList()));
//                                tempWithNoEps.get(productionArray[i]).addAll(xFX);
//                                System.out.println(tempWithNoEps);
//                        		if (!tempWithNoEps.get(productionArray[i]).isEmpty() && !isSubset(tempWithNoEps.get(productionArray[i]), first.get(entry.getKey()))) {
//                        			first.get(entry.getKey()).addAll(tempWithNoEps.get(productionArray[i]));
//                                    change = true;
//                                }
//                        	}
//                        	
//                        		
//                            
//                        }
                        
                      
                    }

                }
            }
            firstFinal =  new LinkedHashMap<>(first);
            return encodeFirst(first);
        }
//        public  String Follow() {
//       	 Map<String, Set<String>> first = new LinkedHashMap<>(firstFinal);
//            Map<String, Set<String>> follow = new LinkedHashMap<>();
//            for (String v : this.variables) {
//                if (v.equals(this.startingVariable)) {
//                    follow.put(v, new HashSet<>(Collections.singleton(EPSILON)));
//                } else
//                    follow.put(v, new HashSet<>(Collections.emptyList()));
//            }
//            for (String t : this.terminals) {
//                follow.put(t, new HashSet<>(Collections.emptyList()));
//            }
//            boolean change = true;
//            while(change) {
//           	 change = false;
//           	 String[] productionArray;
//           	 for (Map.Entry<String, ArrayList<String>> entry : this.rules.entrySet()) {
//           	     for (String production : entry.getValue()) {
//                        productionArray = parser(production);
//                        if (this.variables.contains(productionArray[productionArray.length - 1])) {
//                            follow.get(productionArray[productionArray.length - 1]).addAll(follow.get(entry.getKey()));
//                        }
//                        if (productionArray.length >= 2) {
//                            for(int i = 0;i<productionArray.length-1;i++) {
//                           		Set<String> xFY= new HashSet<String>(first.get(productionArray[i+1]));
//                           		xFY.remove("$");
//                           		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
//                                   tempWithNoEps2.put(productionArray[i+1], new HashSet<>(Collections.emptyList()));
//                                   tempWithNoEps2.get(productionArray[i+1]).addAll(xFY);
//                           	 if (!isSubset(first.get(productionArray[i+1]), follow.get(productionArray[i]))) {
//                                    follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[i + 1]));
//                                    change = true;
//                                }
//                              
//                            }
//                            for(int i = 0;i<productionArray.length;i++) {
//                       		 if(i+1<productionArray.length)
//                           	 if (first.get(productionArray[i + 1]).contains(EPSILON)) {
//                           		 for(int j = i+1;j<productionArray.length;j++) {
//                               		 if(j+1<productionArray.length) {
//                               			 Set<String> xFY= new HashSet<String>(first.get(productionArray[j+1]));
//                                     		xFY.remove("$");
//                                     		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
//                                             tempWithNoEps2.put(productionArray[j+1], new HashSet<>(Collections.emptyList()));
//                                             tempWithNoEps2.get(productionArray[j+1]).addAll(xFY);
//                                			 if (first.get(productionArray[j + 1]).contains(EPSILON)) {
//                                				 if(j+1 == productionArray.length-1) {
//                                                     follow.get(productionArray[i]).addAll(first.get(productionArray[j + 1]));
//                                				 }
//                                				 else {
//                                                     follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[j + 1]));
//                                				 }
//                                			 }
//                                			 else {
//                                                follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[j + 1]));
//                                				 break;
//                                			 }
//                               		 }
//                               		 else {
//                               			 Set<String> xFY= new HashSet<String>(first.get(productionArray[i+1]));
//                                      		xFY.remove("$");
//                                      		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
//                                              tempWithNoEps2.put(productionArray[i+1], new HashSet<>(Collections.emptyList()));
//                                              tempWithNoEps2.get(productionArray[i+1]).addAll(xFY);
//                                            follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[i + 1]));
//                               		 }
//                           
//                           		 }
//                           	 }
//                           	 
//                            }
//                            for(int i = 0;i<productionArray.length-1;i++) {
//                                if (first.get(productionArray[i + 1]).contains(EPSILON)) {
//   	                        	 if (!isSubset(follow.get(entry.getKey()), follow.get(productionArray[i]))) {
//   	                                 follow.get(productionArray[i]).addAll(follow.get(entry.getKey()));
//   	                                 change = true;
//   	
//   	                             }
//   	                        	if(i==0&&i+2<productionArray.length) {
//   	                        		 follow.get(productionArray[i]).addAll(first.get(productionArray[i + 2]));
//   	                        	}
//                                }
//                                
//                            }
//                           
//                        }
//                        else {
//                            follow.get(productionArray[0]).addAll(follow.get(entry.getKey()));
//                        }
//                      
//           	     }
//           	 }
//            }
//            return encodeFollow(follow);
//       }
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
                            		Set<String> xFY= new HashSet<String>(first.get(productionArray[i+1]));
                            		xFY.remove("$");
                            		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
                                    tempWithNoEps2.put(productionArray[i+1], new HashSet<>(Collections.emptyList()));
                                    tempWithNoEps2.get(productionArray[i+1]).addAll(xFY);
                            	 if (!isSubset(first.get(productionArray[i+1]), follow.get(productionArray[i]))) {
                                     follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[i + 1]));
                                     change = true;
                                 }
                               
                             }
                             for(int i = 0;i<productionArray.length;i++) {
                        		 if(i+1<productionArray.length) {
                            	 if (first.get(productionArray[i + 1]).contains(EPSILON)) {
                            		 for(int j = i+1;j<productionArray.length;j++) {
                                		 if(j+1<productionArray.length) {
                                			 Set<String> xFY= new HashSet<String>(first.get(productionArray[j+1]));
                                      		xFY.remove("$");
                                      		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
                                              tempWithNoEps2.put(productionArray[j+1], new HashSet<>(Collections.emptyList()));
                                              tempWithNoEps2.get(productionArray[j+1]).addAll(xFY);
                                 			 if (first.get(productionArray[j + 1]).contains(EPSILON)) {
                                 				 if(j+1 == productionArray.length-1) {
                                                      follow.get(productionArray[i]).addAll(first.get(productionArray[j + 1]));
                                 				 }
                                 				 else {
                                                      follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[j + 1]));
                                 				 }
                                 			 }
                                 			 else {
                                                 follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[j + 1]));
                                 				 break;
                                 			 }
                                		 }
                                		 else {
                                			 if(i+1==productionArray.length-1) {
                                                 follow.get(productionArray[i]).addAll(follow.get(entry.getKey()));

                                			 }
                                				 Set<String> xFY= new HashSet<String>(first.get(productionArray[i+1]));
                                            		xFY.remove("$");
                                            		Map<String, Set<String>> tempWithNoEps2 = new LinkedHashMap<>();
                                                    tempWithNoEps2.put(productionArray[i+1], new HashSet<>(Collections.emptyList()));
                                                    tempWithNoEps2.get(productionArray[i+1]).addAll(xFY);
                                                  follow.get(productionArray[i]).addAll(tempWithNoEps2.get(productionArray[i + 1]));
                                			 
                                			
                                		 }
                            
                            		 }
                            	 }
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
        	ArrayList<String> terms = new ArrayList<String>();
        	for(Map.Entry<String, ArrayList<String>> entry : this.rules.entrySet()){
        	terms.add(entry.getKey());
        	}
            String overall = "";
            boolean startVar = false;
            String start = "";
            for (String v : terms) {
            	if(v.equals("S")) {
            		startVar = true;
            	}
            	String part = "";
            	Object[] firstString= first.get(v).toArray();
            	for(int i = 0 ;i<firstString.length;i++) {
            		if(firstString[i].equals("$")) {
            			part+="e";
//            			System.out.print("e");
            		}
            		else {
            			part+=firstString[i];
//                		System.out.print(firstString[i]);
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
        	ArrayList<String> terms = new ArrayList<String>();
        	for(Map.Entry<String, ArrayList<String>> entry : this.rules.entrySet()){
        	terms.add(entry.getKey());
        	}
            String overall = "";
            boolean startVar = false;
            String start = "";
            for (String v : terms) {
            	if(v.equals("S")) {
            		startVar = true;
            	}
          	  boolean eps = false;
            	String part = "";
            	Object[] firstString= follow.get(v).toArray();
            	for(int i = 0 ;i<firstString.length;i++) {
            		if(firstString[i].equals("$")) {
            			eps = true;
//            			System.out.print("e");
            		}
            		else {
            			part+=firstString[i];
//                		System.out.print(firstString[i]);
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

}
