
package com.company;
public class Stemmer {
    
    public static boolean consonant(char c){
        return c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u'; 
        
    }
    
    public static int getM(String S, int lim){
    
        int l = 0, r = lim - 1, ret = 0;
        for(; l < r;) 
            if(consonant(S.charAt(l)) || (S.charAt(l) == 'y' && l > 0 && !consonant(S.charAt(l - 1))))
                l++;
            else 
                break;
        for(; r > l;)
            if(consonant(S.charAt(r)) || (S.charAt(r) == 'y' && r > 0 && !consonant(S.charAt(r - 1))))
                break;
            else 
                r--;
        if(l==r)
            return 0;
        for(;l <= r;){           
            while(true){
                if(l > r)
                    break;
                if(!(consonant(S.charAt(l)) 
                    || (S.charAt(l) == 'y' && l > 0 && !consonant(S.charAt(l - 1))))) 
                    l++;
                else 
                    break;
            }            
            
            if(l > r)
                break;
            while(true){
                if(l > r)
                    break;
                if(consonant(S.charAt(l)) 
                    || (S.charAt(l) == 'y' && l > 0 && !consonant(S.charAt(l - 1)))) 
                    l++;
                else 
                    break;
            }            
            ret++;
        }
        return ret;
    
    }
    
    public static boolean containsVowel(String S, int r){
        for(int i = 0; i < r; i++)
            if(!(consonant(S.charAt(i)) 
                    || (S.charAt(i) == 'y' && i > 0 && !consonant(S.charAt(i - 1)))))
                return true;
        return false;
    }
    
    public static String removeSuffix(String S, int len){
        return S.substring(0, S.length() - len);
    }
    
    public static String Step1a(String result){
        if(result.endsWith("sses") || result.endsWith("ies")) 
            result = removeSuffix(result, 2);        
        
        else if(result.endsWith("s") && !result.endsWith("ss"))
            result = removeSuffix(result, 1);
         
        return result;
    }
    
    public static String addSuffix(String result, String add){        
        return result.concat(add);
    }
    
    public static boolean endsInDoubleConsonant(String S){
        if(S.length() < 2)return false;
        
        if(S.charAt(S.length() - 1) == S.charAt(S.length() - 2)){
            return consonant(S.charAt(S.length() - 2)) 
                    || (S.charAt(S.length() - 2) == 'y' && S.length() > 2 
                    && !consonant(S.charAt(S.length() - 3)));
        }
        return false;
    }
    
    public static boolean cvc(String S, int len){
        if(len < 3)return false;
        
        for(int i = len - 3; i < len; i += 2){
            if(!(consonant(S.charAt(i)) 
                    || (S.charAt(i) == 'y' && i > 0 && !consonant(S.charAt(i - 1)))))
                return false;            
        }
        
        if(consonant(S.charAt(len - 2)) 
                    || (S.charAt(len - 2) == 'y' && len > 2 
                    && !consonant(S.charAt(len - 3))))
            return false;
        char c = S.charAt(len - 1);
        return !(c == 'w' || c == 'x' || c == 'y');
    }
    
    public static String post1b(String result){
        int repM = getM(result, result.length());
        if(result.endsWith("at") || result.endsWith("bl") || result.endsWith("iz"))
            result = addSuffix(result, "e");
        
        else if(endsInDoubleConsonant(result) 
                && !(result.endsWith("l") || result.endsWith("s") || result.endsWith("z")))
            result = removeSuffix(result, 1);       
        
        else if(repM == 1 && cvc(result, result.length()))
            result = addSuffix(result, "e");
        
        return result;                
    }
    
    public static String Step1b(String result){
        int repM = getM(result, result.length() - 3);
        
        if(result.endsWith("eed") && repM > 0)
            result = removeSuffix(result, 1);
        
        else if(containsVowel(result, result.length() - 2) 
                && result.endsWith("ed") && !result.endsWith("eed")){
            result = removeSuffix(result, 2);
            result = post1b(result);
        }
        else if(containsVowel(result, result.length() - 3) && result.endsWith("ing")){
            result = removeSuffix(result, 3);
            result = post1b(result);
        }
        
        return result;
    }
    
    public static String Step1c(String result){
        if(containsVowel(result, result.length() - 1) && result.endsWith("y")){
            result = removeSuffix(result, 1);
            result = addSuffix(result, "i");
        }
        return result;
    }
    
    public static String Step2(String result){
        String suff[] = {"ational", "tional", "enci", "anci", "izer", "abli", "alli", 
            "entli", "eli", "ousli", "ization", "ation", "ator", "alism", "iveness", 
            "fulness", "ousness", "aliti", "iviti", "biliti"};
        
        String rep[] = {"ate", "tion", "ence", "ance", "ize", "able", "al", "ent", "e",
            "ous", "ize", "ate", "ate", "al", "ive", "ful", "ous", "al", "ive", "ble"};        
        
        for(int i = 0; i < suff.length; i++)
            if(result.endsWith(suff[i])){
                if(getM(result, result.length() - suff[i].length()) > 0){
                    result = removeSuffix(result, suff[i].length());
                    result = addSuffix(result, rep[i]);
                }        
                break;
            }
        return result;
    }
    
    public static String Step3(String result){
        String suff[] = {"icate", "ative", "alize", "iciti", "ical", "ful", "ness"};
        
        String rep[] = {"ic", "", "al", "ic", "ic", "", ""};        
        
        for(int i = 0; i < suff.length; i++)
            if(result.endsWith(suff[i])){
                if(getM(result, result.length() - suff[i].length()) > 0){
                    result = removeSuffix(result, suff[i].length());
                    result = addSuffix(result, rep[i]);
                }        
                break;
            }
        return result;
    }
    
    public static String Step4(String result){
        String suff[] = {"al", "ance", "ence", "er", "ic", "able", "ible", "ant", 
            "ement", "ment", "ent", "ou", "ism", "ate", "iti", "ous", "ive", "ize"};       
        
        if(result.endsWith("ion")){
            if(result.length() > 3 
                    && (result.charAt(result.length() - 4) == 't' 
                    || result.charAt(result.length() - 4) == 's'))
                if(getM(result, result.length() - 3) > 1)
                    result = removeSuffix(result, 3);            
            return result;
        }
        for(int i = 0; i < suff.length; i++)
            if(result.endsWith(suff[i])){
                if(getM(result, result.length() - suff[i].length()) > 1)
                    result = removeSuffix(result, suff[i].length());               
                break;
            }
        return result;
    }
    
    public static String Step5a(String result){
        
        if(result.endsWith("e")){
            int repM = getM(result, result.length() - 1);
            if(repM > 1 || (repM == 1 && !cvc(result, result.length() - 1)))
                result = removeSuffix(result, 1);       
        }
        return result;
    }
    
    public static String Step5b(String result){
        
       int repM = getM(result, result.length());
       if(repM > 1 && endsInDoubleConsonant(result) && result.endsWith("l"))
           result = removeSuffix(result, 1);
       return result;
    }
    
    public static String stem(String in){
        
        //Any word can be represented as [C]VCVC...[V]
        //[] denote arbitrary presence of their contents
        //It can also be represented as [C](VC)^m[V] (m copies of VC)
        
                        
        String result = in;
        result = result.toLowerCase();
        ///////////////////////////////////////////////////////////////
        //Step 1a
        
        result = Step1a(result);
        
        ///////////////////////////////////////////////////////////////
        //Step 1b
        
        result = Step1b(result);
        ///////////////////////////////////////////////////////////////
        //Step 1c
        
        result = Step1c(result);        
        ///////////////////////////////////////////////////////////////
        //Step 2
        
        result = Step2(result);
        ///////////////////////////////////////////////////////////////
        //Step 3
        
        result = Step3(result);
        ///////////////////////////////////////////////////////////////
        //Step 4
        
        result = Step4(result);        
        ///////////////////////////////////////////////////////////////
        //Step 5a
        
        result = Step5a(result);
        ///////////////////////////////////////////////////////////////
        //Step 5b
        
        result = Step5b(result);
        
        return result;
    }
       

    
}
