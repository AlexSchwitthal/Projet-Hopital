/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//test Pull
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author kormli18
 */
public class Hopital {

    private ArrayList<Chirurgie> listeChirurgies;
    private ArrayList<Erreur> listeErreurs;

    public Hopital() {
        this.listeChirurgies = new ArrayList<Chirurgie>();
        this.listeErreurs = new ArrayList<Erreur>();
    }

    public void init(String nomFichier) throws FileNotFoundException, IOException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("." + File.separator + "files" + File.separator + nomFichier)));

        String ligne = reader.readLine();
        while (ligne != null) {
            if (!ligne.contains("ID CHIRURGIE")) {
                String[] tab = ligne.split(";");
                boolean existChirurgien = false;
                boolean existSalle = false;
                Chirurgien chirurgien = null;
                Salle salle = null;
                for (Chirurgie ch : this.listeChirurgies) {
                    if (ch.getChirurgien().getNom().equals(tab[5])) {
                        existChirurgien = true;
                        chirurgien = ch.getChirurgien();
                    }
                    if (ch.getSalle().getNom().equals(tab[4])) {
                        existSalle = true;
                        salle = ch.getSalle();
                    }
                }

                if (!existChirurgien) {
                    chirurgien = new Chirurgien(tab[5]);
                }
                if (!existSalle) {
                    salle = new Salle(tab[4]);
                }
                String[] tabDate = tab[1].split("/");
                LocalDate dateChirurgie = LocalDate.of(Integer.parseInt(tabDate[2]), Integer.parseInt(tabDate[1]), Integer.parseInt(tabDate[0]));

                tabDate = tab[2].split(":");
                LocalTime heureDeb = LocalTime.of(Integer.parseInt(tabDate[0]), Integer.parseInt(tabDate[1]), Integer.parseInt(tabDate[2]));

                tabDate = tab[3].split(":");
                LocalTime heureFin = LocalTime.of(Integer.parseInt(tabDate[0]), Integer.parseInt(tabDate[1]), Integer.parseInt(tabDate[2]));

                this.listeChirurgies.add(new Chirurgie(tab[0], dateChirurgie, heureDeb, heureFin, salle, chirurgien));
            }
            ligne = reader.readLine();
        }
        Collections.sort(this.listeChirurgies);
    }

    public void printListeChirurgies() {
        for (Chirurgie c : this.listeChirurgies) {
            System.out.println(c.toString());
        }
    }
    
    public void printListeErreurs() {
        System.out.println("Nombre d'erreur(s) : " + this.listeErreurs.size());
        for (Erreur e : this.listeErreurs) {
            System.out.println(e.toString());
        }
    }

    /**
     *
     * @return retourne une map ayant pour clé une date
     */
    public TreeMap<LocalDate, ArrayList<Chirurgie>> TriParJour() {
        HashMap<LocalDate, ArrayList<Chirurgie>> m = new HashMap<>();

        LocalDate dateDuJour = LocalDate.now();
        ArrayList<Chirurgie> chirurgieDuJour = new ArrayList<>();

        Iterator<Chirurgie> it = this.listeChirurgies.iterator();
        while (it.hasNext()) {
            Chirurgie c = it.next();
            if (dateDuJour.equals(c.getDate())) {
                chirurgieDuJour.add(c);
            } 
            else {
                if (chirurgieDuJour.size() != 0) {
                    m.put(dateDuJour, chirurgieDuJour);
                }

                dateDuJour = c.getDate();

                chirurgieDuJour = new ArrayList<>();
                chirurgieDuJour.add(c);
            }
            if (!(it.hasNext())) {
                m.put(dateDuJour, chirurgieDuJour);
            }
        }

        TreeMap<LocalDate, ArrayList<Chirurgie>> map = new TreeMap<>(m);
        return map;
    }

    /**
     *
     * @param c1 un Objet Chirurgie
     * @param c2 un Objet Chirurgie, différent de c1
     * @return return un booléen : - true si c1 se déroule en même temps que c2
     * (partiellement ou complètement) - false sinon
     */
    public boolean estParallele(Chirurgie c1, Chirurgie c2) {
        if (c1.getDate().equals(c2.getDate())) {
            if (c1.getHeureDebut().equals(c2.getHeureDebut())) {
                return true;
            }
            if (((c2.getHeureDebut().isAfter(c1.getHeureDebut()))
                    && c2.getHeureDebut().isBefore(c1.getHeureFin()))
                    || ((c1.getHeureDebut().isAfter(c2.getHeureDebut()))
                    && c1.getHeureDebut().isBefore(c2.getHeureFin()))) {
                return true;
            }
        }
        return false;
    }

    public Chirurgie getChirurgieById(String s) {
        for (Chirurgie c : this.listeChirurgies) {
            if (c.getId().equals(s)) {
                return c;
            }
        }
        return null;
    }

    public void findErreur() {
        this.listeErreurs = new ArrayList<>();
        TreeMap<LocalDate, ArrayList<Chirurgie>> dateChirurgies = this.TriParJour();

        for (Map.Entry<LocalDate, ArrayList<Chirurgie>> entree : dateChirurgies.entrySet()) {
            ArrayList<Chirurgie> chirurgiesDuJour = entree.getValue();

            
            for (int i = 0; i < chirurgiesDuJour.size(); i++) {
                Chirurgie c1 = chirurgiesDuJour.get(i);

                if(chirurgiesDuJour.indexOf(c1) != chirurgiesDuJour.size() -1) {
                    Chirurgie c2 = chirurgiesDuJour.get(i+1);
                    if(this.estParallele(c1, chirurgiesDuJour.get(i+1))){
                        if ((c1.getChirurgien().equals(c2.getChirurgien()))
                            && c1.getSalle().equals(c2.getSalle())) {
                            Erreur e = new ErreurChevauchement();
                            e.addChirurgie(c1);
                            e.addChirurgie(c2);
                            this.listeErreurs.add(e);
                        } 
                        else if (c1.getChirurgien().equals(c2.getChirurgien())) {
                            Erreur e = new ErreurUbiquite();
                            e.addChirurgie(c1);
                            e.addChirurgie(c2);
                            this.listeErreurs.add(e);
                        } 
                        else if (c1.getSalle().equals(c2.getSalle())) {
                            Erreur e = new ErreurInterference();
                            e.addChirurgie(c1);
                            e.addChirurgie(c2);
                            this.listeErreurs.add(e);
                        }
                    }
                }
//                while ((chirurgiesDuJour.indexOf(c1) != chirurgiesDuJour.size() - 1) && (this.estParallele(c1, chirurgiesDuJour.get(++i)))) {
//                    Chirurgie c2 = chirurgiesDuJour.get(i);
//
//                    if ((c1.getChirurgien().equals(c2.getChirurgien()))
//                            && c1.getSalle().equals(c2.getSalle())) {
//                        Erreur e = new ErreurChevauchement();
//                        e.addChirurgie(c1);
//                        e.addChirurgie(c2);
//                        this.listeErreurs.add(e);
//                    } 
//                    else if (c1.getChirurgien().equals(c2.getChirurgien())) {
//                        Erreur e = new ErreurUbiquite();
//                        e.addChirurgie(c1);
//                        e.addChirurgie(c2);
//                        this.listeErreurs.add(e);
//                    } 
//                    else if (c1.getSalle().equals(c2.getSalle())) {
//                        Erreur e = new ErreurInterference();
//                        e.addChirurgie(c1);
//                        e.addChirurgie(c2);
//                        this.listeErreurs.add(e);
//                    }
//                    c1 = c2;
//                }
            }
        }
    }

    public void resolveErreur() {
        Iterator<Erreur> it = this.listeErreurs.iterator();
        while (it.hasNext()) {
            Erreur e = it.next();
            ArrayList<Chirurgie> chirurgiesDuJour = e.getListeChirurgiesErreur();
            LocalDate dateDuJour = chirurgiesDuJour.get(0).getDate();
            ArrayList<Chirurgien> chirurgiensDuJour = this.ChirurgiensDuJour(dateDuJour);
            ArrayList<Salle> listeSalles = this.getListeSalles();

            // ----- INTERFERENCE ------ //
            if (e instanceof ErreurInterference) {
                // tentative de recherche d'une autre salle disponible
                if (this.changementSalle(listeSalles, chirurgiesDuJour, dateDuJour)) {} 
                
                // tentative de recherche d'un autre chirurgien disponible
                else if(this.changementChirurgien(chirurgiensDuJour, chirurgiesDuJour, dateDuJour)) {}
                
                // tentative de changement de l'heure de la chirurgie 
                //else if() {}
            } 
            
            // ----- CHEVAUCHEMENT ------ //
            else if (e instanceof ErreurChevauchement) {
                // tentative de recherche d'une autre salle disponible
                if (this.changementSalle(listeSalles, chirurgiesDuJour, dateDuJour)) {}
            } 

            // ----- UBIQUITE ------ //
            else if (e instanceof ErreurUbiquite) {
                // tentative de recherche d'un autre chirurgien disponible
                if (this.changementChirurgien(chirurgiensDuJour, chirurgiesDuJour, dateDuJour)) {}
            }
        }
    }

    public boolean isActifSalle(Salle s, LocalDate jour, LocalTime heureDebut, LocalTime heureFin) {

        for (Chirurgie ch : this.listeChirurgies) {
            if (ch.getDate().equals(jour)) {
                if (ch.getSalle().equals(s)) {
                    if (ch.getHeureDebut().equals(heureDebut)) {
                        return true;
                    }
                    if ((ch.getHeureDebut().isAfter(heureDebut))
                            && ch.getHeureDebut().isBefore(heureFin)
                            || ((heureDebut.isAfter(ch.getHeureDebut()))
                            && heureDebut.isBefore(ch.getHeureFin()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isActifChirurgien(Chirurgien c, LocalDate jour, LocalTime heureDebut, LocalTime heureFin) {

        for (Chirurgie ch : this.listeChirurgies) {
            if (ch.getDate().equals(jour)) {
                if (ch.getChirurgien().equals(c)) {
                    if (ch.getHeureDebut().equals(heureDebut)) {
                        return true;
                    }
                    else if ((ch.getHeureDebut().isAfter(heureDebut))
                            && ch.getHeureDebut().isBefore(heureFin)
                            || ((heureDebut.isAfter(ch.getHeureDebut()))
                            && heureDebut.isBefore(ch.getHeureFin()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Chirurgien> ChirurgiensDuJour(LocalDate ld) {
        ArrayList<Chirurgien> listeChirurgiens = new ArrayList<>();
        for (Chirurgie c : this.listeChirurgies) {
            if (c.getDate().equals(ld)) {
                if (!(listeChirurgiens.contains(c.getChirurgien()))) {
                    listeChirurgiens.add(c.getChirurgien());
                }
            }
        }
        return listeChirurgiens;
    }
    
    public ArrayList<Chirurgie> getChirurgiesDuJour(LocalDate ld){
    	ArrayList<Chirurgie> listeChirurgies = new ArrayList<>();
    	for(Chirurgie c : this.listeChirurgies) {
    		if(c.getDate().equals(ld)) {
    			if (!(listeChirurgies.contains(c))) {
    				listeChirurgies.add(c);
    			}
    		}
    	}
    	return listeChirurgies;
    }

    public ArrayList<Salle> getListeSalles() {
        ArrayList<Salle> listeSalles = new ArrayList<>();
        for (Chirurgie c : this.listeChirurgies) {
            if (!(listeSalles.contains((c.getSalle())))) {
                listeSalles.add(c.getSalle());
            }
        }
        return listeSalles;
    }

     public boolean changementSalle(ArrayList<Salle> listeSalles, ArrayList<Chirurgie> listeChirurgies, LocalDate dateDuJour) {
        for (Salle s : listeSalles) {
            for(Chirurgie ch : listeChirurgies){
                if (!(s.equals(ch.getSalle()))) {
                    if (!(isActifSalle(s, dateDuJour, ch.getHeureDebut(), ch.getHeureFin()))) {
                        ch.setSalle(s);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean changementChirurgien(ArrayList<Chirurgien> listeChirurgiens, ArrayList<Chirurgie> listeChirurgies, LocalDate dateDuJour) {
        for (Chirurgien c : listeChirurgiens) {
            for(Chirurgie ch : listeChirurgies) {
                if (!(c.equals(ch.getChirurgien()))) {
                    if (!(isActifChirurgien(c, dateDuJour, ch.getHeureDebut(), ch.getHeureFin()))) {
                        ch.setChirurgien(c);
                        return true;
                    }
                }
            }
           
        }
        return false;
    }
    
    public LocalTime getDureeMoyenneChirurgie(){
    	float total = 0;
    	for(Chirurgie c : this.listeChirurgies) {
    		Duration d = Duration.between(c.getHeureDebut(), c.getHeureFin());
    		total += (float) d.getSeconds();
    	}
    	float moyenne = total/this.listeChirurgies.size();

    	int min = (int) moyenne/60;
    	min = min % 60;
    	int heure = (int) moyenne/3600;
    	
    	return LocalTime.of(heure, min);
    }
    
    /* A FINIR */
    public void normalisationHeureChirurgie() {
    	LocalTime moyenne = this.getDureeMoyenneChirurgie();
        int moyenneEnSeconde = moyenne.getHour() * 3600 + moyenne.getMinute() * 60 + moyenne.getSecond();
        //System.out.println(moyenneEnSeconde);
    	for(Chirurgie c : this.listeChirurgies) {
            Duration dureeChirurgie = Duration.between(c.getHeureDebut(), c.getHeureFin());
            if(dureeChirurgie.getSeconds() >= moyenneEnSeconde * 1.5) {
                //System.out.println(c);
                
                if(dureeChirurgie.getSeconds() >= ((moyenneEnSeconde * 1.5) + (moyenne.getHour() * 3600) + (moyenne.getMinute() * 60))) {
                	//System.out.println((moyenneEnSeconde * 1.5) + (moyenne.getHour() * 3600) + (moyenne.getMinute() * 60));
                    c.setHeureFin(c.getHeureFin().minusHours(moyenne.getHour()));
                    c.setHeureFin(c.getHeureFin().minusMinutes(moyenne.getMinute()));
                    c.setHeureFin(c.getHeureFin().minusSeconds(moyenne.getSecond()));
                    //System.out.println(c + " Changement wesh wesh");
                }
                else {	
                	
                	/* A FAIRE */
                	long toLong = (long) (moyenneEnSeconde * 1.5);
                	//System.out.println(toLong);
                	c.setHeureFin(c.getHeureDebut().plusSeconds(toLong));
                	c.setHeureFin(LocalTime.of(c.getHeureFin().getHour(), c.getHeureFin().getMinute()));
                	//System.out.println(c + " mdr changement");
                	
                }
            }
    	}
    }
    
    public boolean changementHeureChirurgie(Chirurgie c, int heure, int minute, String typeChangement) {
        
    	minute = minute + (heure * 60);
        LocalTime newHeureDebut = LocalTime.of(0, 0);
        LocalTime newHeureFin = LocalTime.of(0,0);
        
        if(typeChangement.equals("avancer")) {
            newHeureDebut = c.getHeureDebut().minusMinutes(minute);
            newHeureFin = c.getHeureFin().minusMinutes(minute);
        }
        else if(typeChangement.equals("retarder")) {
            newHeureDebut = c.getHeureDebut().plusMinutes(minute);
            newHeureFin = c.getHeureFin().plusMinutes(minute);
        }
        else {
            throw new IllegalArgumentException("le 4ème argument de la methode changementHeureChirurgie doit être \"avancer\" ou \"retarder\"");
        }

    	ArrayList<Chirurgie> listeChirurgiesDuJour = this.getChirurgiesDuJour(c.getDate());
    	Chirurgie tentativeChirurgie = new Chirurgie(c.getId(), c.getDate(), newHeureDebut, newHeureFin, c.getSalle(), c.getChirurgien());	
    	boolean datePossible = true;
        
    	for(Chirurgie ch : listeChirurgiesDuJour) {
            if(this.estParallele(tentativeChirurgie, ch)) {
    		datePossible = false;
            }
    	}
        
    	if(datePossible) {
            c.setHeureDebut(newHeureDebut);
            c.setHeureFin(newHeureFin);
            return true;
    	}
        else {
            return false;
        }
    }
    
    public LocalTime getHeureLimiteDebut() {
    	LocalTime ld = LocalTime.of(23, 59);
    	for(Chirurgie c : this.listeChirurgies) {
    		if(c.getHeureDebut().isBefore(ld)) {
    			ld = c.getHeureDebut();
    		}
    	}
    	return ld;
    }
    
    public LocalTime getHeureLimiteFin() {
    	LocalTime ld = LocalTime.of(0, 1);
    	for(Chirurgie c : this.listeChirurgies) {
    		if(c.getHeureFin().isAfter(ld)) {
    			ld = c.getHeureFin();
    		}
    	}
    	return ld;
    }
    
}
