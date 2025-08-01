package de.uol.swp.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.common.Configuration;
import de.uol.swp.server.action.ActionService;
import de.uol.swp.server.approvable.ApprovableService;
import de.uol.swp.server.card.CardService;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.chat.ChatService;
import de.uol.swp.server.communication.ServerHandler;
import de.uol.swp.server.communication.netty.NettyServerHandler;
import de.uol.swp.server.communication.netty.Server;
import de.uol.swp.server.di.ServerModule;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.game.turn.PlayerTurnService;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.map.MapTypeService;
import de.uol.swp.server.plague.PlagueService;
import de.uol.swp.server.player.AIPlayerService;
import de.uol.swp.server.role.RoleManagement;
import de.uol.swp.server.role.RoleService;
import de.uol.swp.server.user.AuthenticationService;
import de.uol.swp.server.user.UserService;
import io.netty.channel.ChannelHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * This class handles the startup of the server, as well as, the creation of default
 * users while the MainMemoryBasedUserStore is still in use.
 * @see de.uol.swp.server.user.store.MainMemoryBasedUserStore
 */
class ServerApp {

	private static final Logger LOG = LogManager.getLogger(ServerApp.class);

	/**
	 * Main Method
	 * <p>
	 * This method handles the creation of the server components and the start of
	 * the server
	 *
	 * @param args Any arguments given when starting the application e.g. a port
	 *             number
	 */
	public static void main(String[] args) throws Exception {
		int port = - 1;
		if (args.length == 1){
			try{
				port = Integer.parseInt(args[0]);
			}catch(Exception e) {
				//Ignore and use default value
			}
		}
		if (port < 0){
			port = Configuration.getDefaultPort();
		}
		LOG.info("Starting Server on port {}", Optional.of(port));

		Injector injector = Guice.createInjector(new ServerModule());
		createServices(injector);
        ServerHandler serverHandler = injector.getInstance(ServerHandler.class);
        ChannelHandler channelHandler = new NettyServerHandler(serverHandler);
        Server server = new Server(channelHandler);
        server.start(port);
	}

	/**
	 * Helper method to create the services the server uses and for the time being
	 * the test users
	 *
	 * @param injector the Google guice injector used for dependency injection
	 */
	private static void createServices(Injector injector) {
		injector.getInstance(UserService.class);
		injector.getInstance(AuthenticationService.class);
        injector.getInstance(LobbyService.class);
        injector.getInstance(ChatService.class);
		injector.getInstance(ChatManagement.class);
		injector.getInstance(RoleService.class);
		injector.getInstance(RoleManagement.class);
		injector.getInstance(GameService.class);
		injector.getInstance(MapTypeService.class);
		injector.getInstance(PlagueService.class);
		injector.getInstance(CardService.class);
		injector.getInstance(ActionService.class);
		injector.getInstance(ApprovableService.class);
		injector.getInstance(PlayerTurnService.class);
		injector.getInstance(AIPlayerService.class);
	}

}
