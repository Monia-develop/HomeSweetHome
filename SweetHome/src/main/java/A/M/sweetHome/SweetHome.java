package A.M.sweetHome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * Ce plugin permet aux joueurs de se téléporter et de gérer leurs homes (emplacements de téléportation)
 * classe principale du plugin SweetHome
 *
 * @author Monia :)
 * @version 1.0
 */

public final class SweetHome extends JavaPlugin {
    //va stocker l'identifiant du joueur avec l'identifiant de la tâche
    HashMap<UUID, Integer> missionDuJoueurs = new HashMap<>();

    @Override
    public void onEnable() {
        //gère les action des commandes créées dans plugin.yml
        getCommand("sethome").setExecutor(this);
        getCommand("homes").setExecutor(this);
        getCommand("home").setExecutor(this);
        getCommand("delhome").setExecutor(this);
        getServer().getPluginManager().registerEvents(new TeleportationListener(this), this);
        //permet de faire fonctionner le plugin en copiant config.yml
        saveDefaultConfig(); // va permettre de régler la limite des homes et enregistrer la position des joueurs

        getLogger().info("Le plugin est activé");

    }

    /**
     * *
     *
     * @param sender  l'entité qui exécute le commande c'est à dire le joueur ou l'ordi
     * @param command l'utilité de la commande
     * @param label   le nom de la commande
     * @param args    les arguments de la commande
     * @return true si la commande est valide
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player joueur = (Player) sender;

            if (label.equalsIgnoreCase("sethome")) {

                //récupère la position de la maison créé par le joueur
                Location setHome = joueur.getLocation();

                //Si le joueur n'a pas saisit de nom pour le home qu'il a créé il reçoit un message d'erreur
                if (args.length == 0) {
                    joueur.sendMessage("Donnez un nom à votre home !!!");
                    return true;
                }

                List<String> mondeinterdit = getConfig().getStringList("Settings.mondesinterdits");
                if( mondeinterdit != null && mondeinterdit.contains(joueur.getWorld().getName())) {
                    joueur.sendMessage("Il est interdit de créer un home dans le monde " + joueur.getWorld().getName() );
                    return true;
                }

                //prend le 1er argument après la commande sethome et on met en lowerCase pour qu'il y ai pas de doublons
                String nomMaisonJoueur = args[0].toLowerCase();
                String cheminDossier = joueur.getUniqueId() + "." + nomMaisonJoueur;
                //si le joueur possède un dossier alors on calcule le nombre de home qu'il a
                if (getConfig().getConfigurationSection(joueur.getUniqueId().toString()) != null) {
                    int nbHome = getConfig().getConfigurationSection(joueur.getUniqueId().toString()).getKeys(false).size();
                    int limiteDeBaseAutorise = getConfig().getInt("MaximumHome",5);
                    //on peut changer la limite de homes du joueur s'il a la permission jusqu'à 40 maximum
                    for(int i = 5; i<40;i++){
                        if(joueur.hasPermission("homes.max." + i)){
                            limiteDeBaseAutorise = i;
                        }
                    }
                    //on vérifie que la nombre de home ne dépasse pas la limite autorisé seulement si c'est un nouveau home
                    if (nbHome >= limiteDeBaseAutorise && !getConfig().contains(cheminDossier)) {
                        joueur.sendMessage(getConfig().getString("Messages.limitHome")); //on prend le message du fichier config
                        return true;
                    }
                }


//la commande --> getConfig().set(chemin, valeur), permet d'enregistrer les coordoonees dans le fichier config
                //Les .x, .y etc permettent d'enregistrer des sous configurations
                //on enregistre d'abord le nom du monde
                getConfig().set(cheminDossier + ".mondeDuJoueur", setHome.getWorld().getName());
                getConfig().set(cheminDossier + ".x", setHome.getX());
                getConfig().set(cheminDossier + ".y", setHome.getY());
                getConfig().set(cheminDossier + ".z", setHome.getZ());
                saveConfig();
                joueur.sendMessage(getConfig().getString("Messages.setHome"));



                return true;

            } else if (label.equalsIgnoreCase("homes")) {
                //récupère uuid du joueur
                String identifiantJ;
                String msgNoHome;

                //on fait en sorte que l'admin regarde les homes d'un joueur
                if(args.length >0 && joueur.hasPermission("homes.admin")){
                    //on récupère le nom du joueur même s'il est pas connecté sur le serveur
                    identifiantJ = (Bukkit.getOfflinePlayer(args[0])).getUniqueId().toString();
                    msgNoHome = args[0] + " n'a aucun home";
                } else {
                    identifiantJ = joueur.getUniqueId().toString();
                    msgNoHome = "Tu ne possèdes aucun homes";
                }
  //pour éviter les bugs on vérifie que le joueur possède au moins un home, si l'identifiant du joueur est absent de la config alors on sait qu'il n'a pas de homes
                if (!getConfig().contains(identifiantJ)) {
                    joueur.sendMessage(msgNoHome);
                    return true;
                }
                //on crée une interface pour pouvoir cliquer sur nos téléportations directement
                Inventory menuGui = Bukkit.createInventory(null, 45, "Mes homes");
                //on va parcourir les homes du joueur
                for(String nomHome : getConfig().getConfigurationSection(identifiantJ).getKeys(false)){
                    //puis on va attribuer un home a un item
                    ItemStack itemHome = new ItemStack(Material.BAKED_POTATO);
                    ItemMeta metaHome = itemHome.getItemMeta();
                    //l'item qu'on a choisit reçoit le nom du home
                    metaHome.setDisplayName(nomHome);
                    //on attribue une description pour se téléporter
                    ArrayList<String> descriptionHome = new ArrayList<>();
                    descriptionHome.add("clique pour te téléporter!");
                    metaHome.setLore(descriptionHome);
                    itemHome.setItemMeta(metaHome);
                    //on ajoute l'item à l'interface
                    menuGui.addItem(itemHome);
                }

                joueur.openInventory(menuGui);

                String listeHomes = "";
                // avec getKeys(false) on récupère juste les noms et pas les sous catégories
                //getConfigurationSection(identifiantJoueur) permet d'aller directement dans le dossier d'un joueur spécifique et pas de tous les joueurs
                for (String home : getConfig().getConfigurationSection(identifiantJ).getKeys(false)) {
                    listeHomes += home + ",";
                }
                joueur.sendMessage(getConfig().getString("Messages.listeHome") + ":" + listeHomes);

                return true;

            } else if (label.equalsIgnoreCase("home")) {
                //on vérifie si le joueur a saisit le nom d'un home déja existant
                if (args.length == 0) {
                    joueur.sendMessage("Saisissez le nom du home que vous avez créé sinon erreur");
                    return true;
                }

                String nomMaison = args[0].toLowerCase();
                String dossierChemin = joueur.getUniqueId() + "." + nomMaison;
                String nomMonde = getConfig().getString(dossierChemin + ".mondeDuJoueur");


                //on vérifie qu'il y ait bien le dossier dans le fichier config
                if (nomMonde == null) {
                    joueur.sendMessage("Le nom" + nomMonde + "n'existe pas");
                    return true;
                }

                //finalement on récupère les coordoonées de x,y,z
                double x = getConfig().getDouble(dossierChemin + ".x");
                double y = getConfig().getDouble(dossierChemin + ".y");
                double z = getConfig().getDouble(dossierChemin + ".z");

                //Créer une nouvelle location permet de transformer le nom du monde en vrai monde pour téléproter le joueur
                Location home = new Location(org.bukkit.Bukkit.getWorld(nomMonde), x, y, z);

                //on récupère le délai d'attente de 3s que l'on a mit dans config.yml et s'il n'y est pas on utilise la valeur par défaut de 0 par sécurité
                int delaiAttente = getConfig().getInt("Settings.teleportationAttente",0);

                if(delaiAttente > 0){
                    joueur.sendMessage(getConfig().getString("Messages.teleportation"));

                }
                Runnable seTeleporter = new Runnable() {
                    @Override
                    //méthode qu'on va utiliser pour téléporter le joueur
                    public void run() {
                        if(joueur.isOnline()) {
                            joueur.teleport(home);
                            joueur.sendMessage("téléportation réussis avec succès");
                            missionDuJoueurs.remove(joueur.getUniqueId()); // enlève le joueur de la liste une fois la téléportation terminé
                        }
                    }
                };
                //le delai d'attente est initialement a 3 sauf que dans minecraft c'est 20 ticks par secondes, 3 battements de coeurs = 0,15 secondes donc on doit multiplier par 20
                // 0,15 x 20 = 3
                //une fois le temps d'attente finit la téléportation se réalisera
                int missionId = Bukkit.getScheduler().runTaskLater(this, seTeleporter, delaiAttente * 20L).getTaskId();
                //on ajoute l'id de la tache au joueur dans le hashmap
                missionDuJoueurs.put(joueur.getUniqueId(), missionId);
                return true;

            } else if (label.equalsIgnoreCase("delhome")) {
                //on vérifie encore que le joueur a écrit le nom d'un hom
                if (args.length == 0) {
                    joueur.sendMessage("Indiquez le nom d'un home a effacer");
                    return true;
                }

                String aSupprimer = args[0].toLowerCase();
                String DossierCheminSupprimer = joueur.getUniqueId() + "." + aSupprimer;
                String nomMondeSupprimer = getConfig().getString(DossierCheminSupprimer + ".mondeDuJoueur");

                if (nomMondeSupprimer == null) {
                    joueur.sendMessage("Le nom" + aSupprimer + "n'existe pas");
                    return true;
                } else {
                    getConfig().set(DossierCheminSupprimer, null);
                    joueur.sendMessage(aSupprimer + getConfig().getString("Messages.deleteHome"));
                    saveConfig(); // si on le met pas lors d'un redémarrage par exemple le home réapparaitra
                }
                return true;
                //le resethome ne marchera que si l'admin l'éxécute
            }  else if (label.equalsIgnoreCase("resethome")) {
                //si l'admin n'écrit aucun joueur pour réinitaliser ses homes, cela ne marchera pas
                if (args.length == 0) {
                    joueur.sendMessage("Indiquez le nom joueur pour réinitialiser ses homes");
                    return true;
                }
                if(!joueur.hasPermission("homes.admin") && !joueur.isOp()){
                    joueur.sendMessage("tu n'as pas la permission d'utiliser cette commande");
                    return true;
                }
                String nomJoueur = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                //on supprime tous les homes du joueur ici
                getConfig().set(nomJoueur,null);
                saveConfig();
                joueur.sendMessage("Tous les homes de" + args[0] + "ont été réinitialisé");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        getLogger().info("La téléportation a été désactivé");
    }
}
