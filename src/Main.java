
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kormli18
 */
public class Main {
      
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException, ChirurgienInexistantException, SalleInexistanteException {
        
//        Menu m = new Menu();
//        while(true) {
//            m.displayMenu();
//            m.switchChoix();
//        }
       //test nouvelle branche
        Hopital h = new Hopital();
//        
//
        String fichierBase = "Chirurgies_v2";
        try {
            h.init(fichierBase + ".csv");
        } 
        catch (ChirurgienInexistantException | SalleInexistanteException | IOException | ParseException e) {			
            e.printStackTrace();
        }        
        h.TriParJour();
        h.findErreur();
        //System.out.println(h.getTailleListeErreurs());
//        
//        h.verificationCouple();
//        //h.printListeChirurgies();
//
//        
////        //LocalTime moyenne = h.getDureeMoyenneChirurgie();
////        //System.out.println(moyenne.getMinute());
////        //Chirurgie c1 = h.getChirurgieById("13");
////        //h.printListeChirurgies();
////        //System.out.println(c1);
////        //System.out.println(h.getChirurgieById("14"));
////        //System.out.println("\n\n");
////        //System.out.println(h.changementHeureChirurgie(h.getChirurgieById("13"), 13, 0, "retarder"));
//////        System.out.println(h.getChirurgieById("6"));
//////        h.changementHeureChirurgie(h.getChirurgieById("6"), 0, 240, "retarder");
//////        System.out.println(h.getChirurgieById("6"));
//////        
//////        
////        
////        //System.out.println(h.getDureeMoyenneChirurgie());
////       
        	h.normalisationHeureChirurgie();
////        
////        //h.printListeChirurgies();
//////        Chirurgie c2 = h.getChirurgieById("25");
//////
//////
//        h.findErreur();
//        
//        
//        //h.printListeErreurs();
        System.out.println("nombres d'erreurs dans le fichier : " + h.getTailleListeErreurs() + "\n\n");
        int nbEtape = 1;
        ArrayList<Integer> nbErreursParEtape = new ArrayList<>();
        while(h.getTailleListeErreurs() > 0) {
            nbErreursParEtape.add(h.getTailleListeErreurs());
            if(nbErreursParEtape.size() > 2) {
                if(((nbErreursParEtape.get(nbErreursParEtape.size() - 1)).equals(nbErreursParEtape.get(nbErreursParEtape.size() - 2)))
                    && ((nbErreursParEtape.get(nbErreursParEtape.size() - 1)).equals(nbErreursParEtape.get(nbErreursParEtape.size() - 3)))) {
                    
                        System.out.println("Impossible de resoudre toutes les erreurs");
                        break;
                }
            }
            System.out.println( " étape " + (nbEtape) + " :");
            h.findErreur();
//           
            h.resolveErreur();
            System.out.println("erreur(s) restante(s) : " + h.getTailleListeErreurs());
            nbEtape++;
            
           
//
//
        }
        
        LocalTime lt1 = LocalTime.of(20, 0);
        LocalTime lt2 = LocalTime.of(0, 0);
        
        if(lt1.isBefore(lt2)) {
            System.out.println(Duration.between(lt1, lt2).toMinutes());
        }
        else {
        	
        }

        
        String time = "22:00-00:30";
        String[] parts = time.split("-");

        LocalTime start = LocalTime.parse(parts[0]);
        LocalTime end = LocalTime.parse(parts[1]);
        if (start.isBefore(end)) { // normal case
            System.out.println(Duration.between(start, end));
        } else { // 24 - duration between end and start, note how end and start switched places
           Duration wesh = Duration.ofMinutes(1440).minus(Duration.between(end, start));
           Long mdr = wesh.toMinutes();
           System.out.println(mdr);
        }
        
        System.out.println(start.plusMinutes(185));
        
        Chirurgie c = h.getChirurgieById("11941");
        System.out.println(c);
        System.out.println(c.getDuree());
        

//////
//        System.out.println("\n\n\n\n LISTE FIN : \n");
//        
//        //h.printListeChirurgies();
////        for(Chirurgie c : h.getListeChirurgies()) {
////        	System.out.println(c.toString());
////        }
////        
////        
//        try {
//            createOutput(h);	
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        h.verificationCouple();
    }
}
