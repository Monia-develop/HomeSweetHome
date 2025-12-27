package A.M.sweetHome;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportationListener implements Listener {
    private final SweetHome plugin;

    public TeleportationListener(SweetHome plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    //la méthode annule la téléportation du joueur s'il bouge
    public void MovementPlayer(PlayerMoveEvent pme) {
        Player joueur = pme.getPlayer();
        if (plugin.missionDuJoueurs.containsKey(joueur.getUniqueId())) { //on vérifie si le joueur a une téléportation en cours
            if (pme.getFrom().distance(pme.getTo()) >= 0.2) { //puis on vérifie si le joueur bouge durant la téléportation
                //On annule la tache en récupérant son identifiant
                int idMission = plugin.missionDuJoueurs.get(joueur.getUniqueId());
                Bukkit.getScheduler().cancelTask(idMission);
                //il faut pas oublier de l'enlever de la liste du joueur a partir de son identifiant
                plugin.missionDuJoueurs.remove(joueur.getUniqueId());
                joueur.sendMessage("La téléportation a été annulée car vous avez bougé");
            }
        }
    }

    @EventHandler
    //la méthode annule la téléportation du joueur s'il subit des dégâts
    public void PlayerDegat(org.bukkit.event.entity.EntityDamageEvent damageEvent) {
        if(damageEvent.getEntity() instanceof Player){
        Player joueur = (Player) damageEvent.getEntity();
        if (plugin.missionDuJoueurs.containsKey(joueur.getUniqueId())) { //on vérifie si le joueur a une téléportation en cours//puis on vérifie si le joueur bouge durant la téléportation
                //On annule la tache en récupérant son identifiant
                int idMission = plugin.missionDuJoueurs.get(joueur.getUniqueId());
                Bukkit.getScheduler().cancelTask(idMission);
                //il faut pas oublier de l'enlever de la liste du joueur a partir de son identifiant
                plugin.missionDuJoueurs.remove(joueur.getUniqueId());
                joueur.sendMessage("Votre téléportation a été annulée car vous avez subit des dégâts");
            }
        }
    }

    @EventHandler
    public void onClickPotato(InventoryClickEvent potato) {

        if(potato.getView().getTitle().equals("Mes homes")){
            if(potato.getCurrentItem() == null || potato.getCurrentItem().getType() == Material.AIR){
                return;
            }
            //récupère la personne qui a cliqué sur la patate
            Player joueur = (Player) potato.getWhoClicked();
            //on récupère le nom du home qui est écrit sur la patate
            String nomHome = potato.getCurrentItem().getItemMeta().getDisplayName();
            //on éxécuté la commande automatiquement une fois la patate cliqué
            joueur.performCommand("home " + nomHome);
            joueur.closeInventory();
        }
    }

}
