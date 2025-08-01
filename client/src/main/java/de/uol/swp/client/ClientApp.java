package de.uol.swp.client;


import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.communication.ClientConnection;
import de.uol.swp.client.communication.ClientConnectionFactory;
import de.uol.swp.client.communication.ConnectionListener;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.di.FXMLLoaderProvider;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.env.EnvReader;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.RegisterUserExceptionResponse;
import de.uol.swp.common.user.response.RegisterUserSuccessResponse;
import de.uol.swp.common.user.server_message.LogoutServerMessage;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * The application class of the client
 * <p>
 * This class handles the startup of the application, as well as, incoming login
 * and registration responses and error messages
 *
 * @see ConnectionListener
 * @see javafx.application.Application
 */
public class ClientApp extends Application implements ConnectionListener {

	private static final Logger LOG = LogManager.getLogger(ClientApp.class);

	private String host;
	private int port;

	private ClientUserService userService;

	@Getter
	private User user;

	private ClientConnection clientConnection;

	private EventBus eventBus;

	private EnvReader envReader;

	private SceneManager sceneManager;

	@Override
	public void start(Stage primaryStage) {
		Injector injector = Guice.createInjector(new ClientModule(this));

		AbstractPresenter.setFxmlLoaderProvider(new FXMLLoaderProvider() {
			@Override
			public FXMLLoader get() {
				return injector.getInstance(FXMLLoader.class);
			}
		});

        this.userService = injector.getInstance(ClientUserService.class);

		eventBus = injector.getInstance(EventBus.class);
		eventBus.register(this);

		envReader = injector.getInstance(EnvReader.class);
		host = envReader.readString("HOST");
		port = envReader.readInt("PORT");
		LOG.info("Using default port {} {}", port, host);

		SceneManagerFactory sceneManagerFactory = injector.getInstance(SceneManagerFactory.class);
		this.sceneManager = sceneManagerFactory.create(primaryStage);

		ClientConnectionFactory connectionFactory = injector.getInstance(ClientConnectionFactory.class);
		clientConnection = connectionFactory.create(host, port);
		clientConnection.addConnectionListener(this);
		Thread t = new Thread(() -> {
			try {
				clientConnection.start();
			} catch (InterruptedException e) {
				exceptionOccurred(e.getMessage());
				Thread.currentThread().interrupt();
			}
		});
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void connectionEstablished(Channel ch) {
		sceneManager.showLoginScreen();
	}

	@Override
	public void stop() throws InterruptedException {
		if (userService != null && user != null) {
			userService.logout(user);
			user = null;
		}
		eventBus.unregister(this);
		LOG.trace("Trying to shutting down client ...");
		if (clientConnection != null) {
			clientConnection.close();
		}
		LOG.info("ClientConnection shutdown");
	}

	/**
	 * Handles successful login
	 * <p>
	 * If an LoginSuccessfulResponse object is detected on the EventBus this
	 * method is called. It tells the SceneManager to show the main menu and sets
	 * this clients user to the user found in the object. If the loglevel is set
	 * to DEBUG or higher "user logged in successfully " and the username of the
	 * logged in user are written to the log.
	 *
	 * @param message The LoginSuccessfulResponse object detected on the EventBus
	 * @see de.uol.swp.client.SceneManager
	 */
	@Subscribe
	public void onLoginSuccessfulResponse(LoginSuccessfulResponse message) {
		LOG.debug("user logged in successfully {}", message.getUser().getUsername());
		this.user = message.getUser();
		sceneManager.showMainScreen(user);
	}

	/**
	 * Handles successful logout
	 * <p>
	 * If an UserLoggedOutMessage object is detected on the EventBus this
	 * method is called. If the loglevel is set to DEBUG or higher "user
     * logged out successfully " and the username of the logged out user
     * are written to the log.
	 *
	 * @param message The UserLoggedOutMessage object detected on the EventBus
	 * @see de.uol.swp.client.SceneManager
	 */
	@Subscribe
	public void onLogoutSuccessfulResponse(LogoutServerMessage message) {
		LOG.debug("user logged out successfully {}", message.getUser().getUsername());
		this.user = null;
	}

	/**
	 * Handles unsuccessful registrations
	 * <p>
	 * If an RegistrationExceptionMessage object is detected on the EventBus this
	 * method is called. It tells the SceneManager to show the sever error alert.
	 * If the loglevel is set to Error or higher "Registration error " and the
	 * error message are written to the log.
	 *
	 * @param message The RegistrationExceptionMessage object detected on the EventBus
	 * @see de.uol.swp.client.SceneManager
	 */
	@Subscribe
	public void onRegistrationExceptionMessage(RegisterUserExceptionResponse message){
		sceneManager.showServerError(String.format("Registration error %s", message));
		LOG.error("Registration error {}", message);
	}

	/**
	 * Handles successful registrations
	 * <p>
	 * If an RegistrationSuccessfulResponse object is detected on the EventBus this
	 * method is called. It tells the SceneManager to show the login window. If
	 * the loglevel is set to INFO or higher "Registration Successful." is written
	 * to the log.
	 *
	 * @param message The RegistrationSuccessfulResponse object detected on the EventBus
	 * @see de.uol.swp.client.SceneManager
	 */
	@Subscribe
	public void onRegistrationSuccessfulMessage(RegisterUserSuccessResponse message) {
		LOG.info("Registration successful.");
		sceneManager.showLoginScreen();
	}

	@Override
	public void exceptionOccurred(String e) {
		sceneManager.showServerError(e);
	}
	/**
	 * Default startup method for javafx applications
	 *
	 * @param args Any arguments given when starting the application
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
