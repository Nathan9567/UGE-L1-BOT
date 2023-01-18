package fr.nathan.ugebot.functions;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class PermissionError {

	public void PermCatch(String perm, User u, TextChannel t) {
		String msg = "Permission insuffisante. Permission nécessaire : ";
		try {
			switch (perm) {
			case "BAN_MEMBERS":
				msg = msg + "Bannir des membres.";
				break;

			case "CREATE_INSTANT_INVITE":
				msg = msg + "Créer une invitation.";
				break;

			case "KICK_MEMBERS":
				msg = msg + "Expulser des membres.";
				break;

			case "MANAGE_CHANNEL":
				msg = msg + "Gérer les salons.";
				break;

			case "MANAGE_EMOTES":
				msg = msg + "Gérer les émojis";
				break;

			case "MANAGE_PERMISSIONS":
				msg = msg + "Gérer les permissions.";
				break;

			case "MANAGE_ROLES":
				msg = msg + "Gérer les rôles.";
				break;

			case "MANAGE_SERVER":
				msg = msg + "Gérer le serveur.";
				break;

			case "MANAGE_WEBHOOKS":
				msg = msg + "Gérer les webhooks.";
				break;

			case "MESSAGE_ADD_REACTION":
				msg = msg + "Ajouter des réactions.";
				break;

			case "MESSAGE_ATTACH_FILES":
				msg = msg + "Joindre des fichiers.";
				break;

			case "MESSAGE_EXT_EMOJI":
				msg = msg + "Utiliser des émojis externes.";
				break;

			case "MESSAGE_HISTORY":
				msg = msg + "Voir les anciens messages.";
				break;

			case "MESSAGE_MANAGE":
				msg = msg + "Gérer les messages.";
				break;

			case "MESSAGE_MENTION_EVERYONE":
				msg = msg + "Mentionner @everyone, @here.";
				break;

			case "MESSAGE_READ":
				msg = msg + "Lire les messages.";
				break;

			case "MESSAGE_TTS":
				msg = msg + "Envoyer des messages TTS.";
				break;

			case "MESSAGE_WRITE":
				u.openPrivateChannel().queue((channel) -> {
					channel.sendMessage("Permission insuffisante. Permission nécessaire : Envoyer des messages.")
							.queue();
				});
				break;

			case "NICKNAME_CHANGE":
				msg = msg + "Changer de pseudo.";
				break;

			case "NICKNAME_MANAGE":
				msg = msg + "Gérer les pseudos.";
				break;

			case "PRIORITY_SPEAKER":
				msg = msg + "Voix prioritaire.";
				break;

			case "VIEW_AUDIT_LOGS":
				msg = msg + "Voir les logs du serveur.";
				break;

			case "VIEW_CHANNEL":
				msg = msg + "Voir les salons.";
				break;

			case "VOICE_CONNECT":
				msg = msg + "Se connecter.";
				break;

			case "VOICE_DEAF_OTHERS":
				msg = msg + "Mettre en sourdine des membres.";
				break;

			case "VOICE_MOVE_OTHERS":
				msg = msg + "Déplacer des membres.";
				break;

			case "VOICE_MUTE_OTHERS":
				msg = msg + "Couper le micro de membres.";
				break;

			case "VOICE_SPEAK":
				msg = msg + "Parler.";
				break;

			case "VOICE_STREAM":
				msg = msg + "Video.";
				break;

			case "VOICE_USE_VAD":
				msg = msg + "Utiliser la Détection de la voix.";
				break;

			}

			if (msg != "Permission insuffisante. Permission nécessaire : ") {
				t.sendMessage(msg).complete();
			}

		} catch (InsufficientPermissionException except) {
			if (except.getPermission().toString() == "MESSAGE_WRITE") {
				u.openPrivateChannel().queue((channel) -> {
					channel.sendMessage("Permission insuffisante. Permission nécessaire : Envoyer des messages.")
							.queue();
				});
			}
			return;
		}
	}

}
