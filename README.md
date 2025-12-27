Description :

SweetHome est un plugin de téléportation développé pour les serveurs Spigot et Paper.
Il permet aux joueurs de créer des homes c'est à dire des points de téléportation. Le plugin propose aussi une interface graphique qui affiche la liste de homes de chaque joueur.
Il leur suffit de cliquer sur une patate pour voir le nom de l'endroit et s'y téléporter instantanément.
Le plugin inclut aussi des commandes pour les administrateurs, leur permettant de gérer facilement les téléportations et les données des joueurs sur le serveur.


Fonctionnalités du plugin : 

- Système de Homes : Créez, supprimez et téléportez-vous vers vos points favoris.
- Menu GUI interactif : utilisez /homes pour utilisez vos patates afin de visualiser et sélectionner vos homes.
- Sécurité Anti-Abus : Délai de téléportation de 3 secondes et annulation de la téléportation si le joueur bouge ou subit des dégâts.
- Limites de homes et on peut augmtenr la limite


Commandes du jeu :
 
/sethome nom :	créé un nouveau home a l'emplacement ou le joueur se trouve 

/home nom : se téléporter à un home	

/homes	: ouvre l'interface graphique	et donne aussi la liste des homes d'un joueur dans le chat

/delhome : supprime un home

/resethome joueur	: supprime tous les homes d'un joueur (seul l'administrateur peut le faire)

Limites de homes : utilisez la permission homes.max.n pour définir une limite de homes pour un joueur. 

--> lp user MoniaChipmunk permission set homes.max.20 true (à mettre dans le terminale)

même chose pour le resethome -->  lp user MoniaChipmunk permission set homes.admin true 

