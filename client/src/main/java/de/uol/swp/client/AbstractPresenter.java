package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.di.FXMLLoaderProvider;
import de.uol.swp.client.user.ClientUserService;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the base for creating a new Presenter.
 *
 * <p>
 * This class prepares the child classes to have the UserService and EventBus set
 * in order to reduce unnecessary code repetition.
 * </p>
 *
 * @author Marco Grawunder
 * @since 2019-08-29
 */
public abstract class AbstractPresenter {
    private static final Logger LOG = LogManager.getLogger(AbstractPresenter.class);
    private static FXMLLoaderProvider fxmlLoaderProvider;

    public static final String DEFAULT_FXML_FOLDER_PATH = "/fxml";
    public static final String PROJECT_ROOT_PACKAGE_NAME = "de.uol.swp.client";
    public static final String DEFAULT_FXML_FILE_SUFFIX = ".fxml";

    /**
     * Sets the {@link #fxmlLoaderProvider}
     *
     * <p>
     *     If the {@link #fxmlLoaderProvider} is null and the given {@code provider} is not null,
     *     it sets {@link #fxmlLoaderProvider} to {@code provider}.
     * </p>
     *
     * @param provider The provider to set
     * @see FXMLLoaderProvider
     * @see ClientApp#start(Stage)
     * @since 2024-09-17
     */
    public static void setFxmlLoaderProvider(final FXMLLoaderProvider provider) {
        if (fxmlLoaderProvider == null && provider != null) {
            fxmlLoaderProvider = provider;
        }
    }

    /**
     * Loads the FXML file associated with the given {@code presenterClass} and returns the associated presenter
     *
     * <p>
     *     After loading the parent element of the fxml file,
     *     a scene is created with it using {@link #createScene(Parent)}.
     * </p>
     *
     * @param presenterClass Class of the presenter to load the FXML file for
     * @return Presenter for loaded FXML file
     * @param <T> Type of the presenter to return
     * @throws RuntimeException if error occurs during loading
     * @see #createPresenter(Class)
     * @see #getFXMLFilePath()
     * @see #createScene(Parent)
     * @see FXMLLoader
     * @see Platform#runLater(Runnable)
     * @implNote Must be called on FX application thread for {@link javafx.scene.web.WebView}
     * @since 2024-09-17
     */
    public static <T extends AbstractPresenter> T loadFXMLPresenter(final Class<T> presenterClass) throws RuntimeException {
        try {
            final T createdPresenter = createPresenter(presenterClass);
            return initializePresenter(createdPresenter, false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            final String message = String.format("Failed to create presenter \"%s\": %s", presenterClass.getSimpleName(), e.getMessage());
            LOG.error(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Returns a new instance of {@code presenterClass}
     *
     * <p>
     *     The {@code presenterClass} must have a default constructor.
     *     Otherwise, this method will throw an exception.
     * </p>
     *
     * @param presenterClass Presenter class to create an instance of
     * @return Instance of {@code presenterClass}
     * @param <T> Type of returned presenter object
     * @throws Exception if there is no default constructor or it is not accessible or there is some other error
     * @see Class#getDeclaredConstructor(Class[])
     * @see java.lang.reflect.Constructor#newInstance(Object...)
     * @since 2024-09-17
     */
    private static <T extends AbstractPresenter> T createPresenter(final Class<T> presenterClass) throws Exception {
        return presenterClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Initializes the presenter by loading the associated FXML file and setting up the scene.
     *
     * <p>
     * This method creates an FXMLLoader for the given presenter, sets the controller if specified,
     * loads the FXML file, and creates the scene for the presenter.
     * </p>
     *
     * @param presenter the presenter to initialize
     * @param setGivenPresenter if true, sets the given presenter as the controller; otherwise, uses the controller from the FXML file
     * @param <T> the type of the presenter
     * @return the initialized presenter
     * @throws RuntimeException if an error occurs during loading the FXML file
     * @see FXMLLoader
     * @see Parent
     * @since 2024-11-05
     */
    protected static <T extends AbstractPresenter> T initializePresenter(T presenter, final boolean setGivenPresenter) {
        String filePath="";
        try {
            final FXMLLoader fxmlLoader = createFXMLLoaderForPresenter(presenter);
            filePath = fxmlLoader.getLocation().getPath();

            if(setGivenPresenter) {
                fxmlLoader.setController(presenter);
            }
            final Parent root = fxmlLoader.load();

            if(!setGivenPresenter) {
                presenter = fxmlLoader.getController();
            }
            presenter.createScene(root);
            return presenter;
        }catch (Exception e) {
            final String message = String.format("Failed to load FXML file \"%s\": %s", filePath, e.getMessage());
            LOG.error(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Creates an FXMLLoader for the given presenter.
     *
     * <p>
     * This method sets the location of the FXMLLoader to the FXML file path associated with the presenter.
     * </p>
     *
     * @param presenter the presenter for which to create the FXMLLoader
     * @return the FXMLLoader for the given presenter
     * @see FXMLLoader
     * @see URL
     * @since 2024-11-05
     */
    private static FXMLLoader createFXMLLoaderForPresenter(final AbstractPresenter presenter) {
        String filepath = presenter.getFXMLFilePath();
        final FXMLLoader fxmlLoader = fxmlLoaderProvider.get();
        final URL url = presenter.getClass().getResource(filepath);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }

    @Inject
    protected ClientUserService userService;
    protected EventBus eventBus;
    protected List<AbstractPresenter> associatedPresenters = new ArrayList<>();
    @Getter
    protected Scene scene;
    protected Stage stage;

    /**
     * Returns path to associated FXML file
     *
     * <p>
     *     By default, the file is assumed to be in {@value DEFAULT_FXML_FOLDER_PATH} directory and
     *     to be called like the associated presenter replacing "Presenter" with "View" and
     *     ending in {@value DEFAULT_FXML_FILE_SUFFIX}.
     * </p>
     *
     * @return Path to associated FXML file
     * @see #getFXMLFolderPath()
     * @since 2024-09-17
     */
    public String getFXMLFilePath() {
        return getFXMLFolderPath() + getFXMLFileName() + DEFAULT_FXML_FILE_SUFFIX;
    }

    /**
     * Returns the path to the folder of the associated FXML file
     *
     * <p>
     *     By default, returns {@value #DEFAULT_FXML_FOLDER_PATH}
     *     with the package path of this presenter class under the {@code client} package.
     * </p>
     *
     * @return Path to the folder of the associated FXML file
     * @since 2024-09-17
     */
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + getPackagePathUnderProjectRoot() + "/";
    }

    /**
     * Returns the package path of this class under the project root package.
     *
     * @return package path of this class under the project root package
     */
    public String getPackagePathUnderProjectRoot() {
        return getClass().getPackageName()
                .replace(PROJECT_ROOT_PACKAGE_NAME, "")
                .replace(".", "/");
    }

    /**
     * Returns the file name for the associated fxml file.
     *
     * <p>
     *     By default, returns the class name of this {@link Class} with {@code Presenter} being replace with {@code View}.
     * </p>
     *
     * @return file name for the associated fxml file
     */
    public String getFXMLFileName() {
        return getViewNameForPresenterName(getClass().getSimpleName());
    }

    /**
     * Returns the name of a view for a given presenter name.
     *
     * @param presenterName presenter name to return a view name for
     * @return name of a view for a given presenter name
     */
    protected String getViewNameForPresenterName(final String presenterName) {
        return presenterName.replace("Presenter", "View");
    }

    /**
     * <p>
     *     Returns the width the associated {@link #scene} is created with
     * </p>
     *
     * <p>
     *     Default is <code>-1</code>, being ignored in {@link #createScene(Parent)}.
     * </p>
     *
     * @return Width of the {@link #scene} set in {@link #createScene(Parent)}
     * @see Scene#Scene(Parent, double, double)
     * @since 2024-09-17
     */
    public double getWidth() {
        return -1;
    }

    /**
     * <p>
     *     Returns the height the associated {@link #scene} is created with
     * </p>
     *
     * <p>
     *     Default is <code>-1</code>, being ignored in {@link #createScene(Parent)}.
     * </p>
     *
     * @return Height of the {@link #scene} set in {@link #createScene(Parent)}
     * @see Scene#Scene(Parent, double, double)
     * @since 2024-09-17
     */
    public double getHeight() {
        return -1;
    }

    /**
     * Creates a {@link Scene} with the given {@code root} and sets it to {@link #scene}
     *
     * <p>
     *     If the return values of both {@link #getWidth()} and {@link #getHeight()} are greater than {@code 0},
     *     they are set as width and height of the new {@link #scene}.
     * </p>
     *
     * <p>
     *     Also adds the stylesheet specified on the {@link SceneManager} to the stylesheets of the {@link #scene}.
     * </p>
     *
     * @param root Root node of {@link #scene}
     * @see #getWidth()
     * @see #getHeight()
     * @see Scene
     * @see SceneManager#STYLE_SHEET
     * @since 2024-09-17
     */
    protected void createScene(final Parent root) {
        final double width = getWidth();
        final double height = getHeight();
        if (width > 0 && height > 0) {
            this.scene = new Scene(root, width, height);
        } else {
            this.scene = new Scene(root);
        }
        this.scene.getStylesheets().add(SceneManager.STYLE_SHEET);
    }

    /**
     * Opens the {@link #scene} on a new {@link Stage} (window) and sets the {@code icon} as the icon
     *
     * @param icon Icon of the new window
     * @see #openInNewWindow()
     * @see Stage
     * @see Stage#getIcons()
     * @since 2024-09-17
     */
    public void openInNewWindow(final Image icon) {
        openInNewWindow();
        Platform.runLater(() -> this.stage.getIcons().add(icon));
    }

    /**
     * Opens the {@link #scene} on a new {@link Stage} (window)
     *
     * @see #openInNewWindow()
     * @see Stage
     * @since 2024-09-17
     */
    public void openInNewWindow() {
        createStage();
        Platform.runLater(() -> this.stage.show());
    }

    /**
     * Creates a new {@link Stage} and sets it to {@link #stage}
     * 
     * @see Stage
     * @see #setStage(Stage)
     * @since 2024-09-17
     */
    public void createStage() {
        Platform.runLater(() -> {
            final Stage createdStage = new Stage();
            setStage(createdStage);
        });
    }

    /**
     * Sets the {@link #stage}
     *
     * <p>
     *     Calls {@link #setStage(Stage, boolean)} with {@code assignOnCloseStageEvent} as {@code true},
     *     setting the close listener of {@link #stage} to {@link #onCloseStageEvent(WindowEvent)}.
     * </p>
     *
     * @param stage Stage to set
     * @see #setStage(Stage, boolean)
     * @since 2024-09-26
     */
    public void setStage(final Stage stage) {
        setStage(stage, true);
    }

    /**
     * Sets the {@link #stage}
     *
     * <p>
     *     Populates the {@link #stage} field and sets the {@link #scene} to it.
     *     Also sets a listener to the stage and executes {@link #onCloseStageEvent(WindowEvent)} when closing it.
     * </p>
     *
     * @param stage Stage to set
     * @param assignOnCloseStageEvent Sets the {@link #stage} close listener to {@link #onCloseStageEvent(WindowEvent)}
     * @see Stage#setScene(Scene)
     * @see Stage#setOnCloseRequest(EventHandler)
     * @see #onCloseStageEvent(WindowEvent)
     * @since 2024-09-26
     */
    public void setStage(final Stage stage, final boolean assignOnCloseStageEvent) {
        this.stage = stage;
        Platform.runLater(() -> this.stage.setScene(this.scene));
        if (assignOnCloseStageEvent) {
            stage.setOnCloseRequest(this::onCloseStageEvent);
        }
    }

    /**
     * Executed when closing the window of {@link #stage}
     *
     * <p>
     *     When closing the {@link #stage} window, this method is called.
     *     It clears the {@link #eventBus}.
     * </p>
     *
     * @param event The {@link WindowEvent} closing the {@link #stage}
     * @see WindowEvent
     * @see Stage#setOnCloseRequest(EventHandler)
     * @see #stage
     * @see #clearEventBus()
     * @since 2024-09-17
     */
    protected void onCloseStageEvent(final WindowEvent event) {
        clearEventBus();
    }

    /**
     * Creates a {@link WindowEvent} on the {@link #stage} to close it.
     *
     * <p>
     *     An event is fired instead of using {@link Stage#close()}
     *     because the latter does not trigger the EventHandler associated with {@link Stage#setOnCloseRequest(EventHandler)}.
     * </p>
     *
     * @see Stage#close()
     * @see Stage#setOnCloseRequest(EventHandler)
     * @see WindowEvent
     * @since 2024-09-13
     */
    protected void closeStage() {
        Platform.runLater(() -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    /**
     * Sets the field eventBus
     *
     * <p>
     * This method sets the field eventBus to the EventBus given via parameter.
     * Afterwards it registers this class to the new EventBus.
     * </p>
     *
     * @implNote This method does not unregister this class from any EventBus it
     *           may already be registered to.
     * @param eventBus The EventBus this class should use.
     * @since 2019-08-29
     */
    @Inject
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        try {
            eventBus.register(this);
        }catch(Exception e){
            // register looks for @Subscribe that is not available in all classes
            LOG.warn("This class does not provide @Subscribe methods ..."+this.getClass());
        }
    }

    /**
     * Clears the field eventBus
     *
     * <p>
     *     This method unregisters this presenter object and all {@link #associatedPresenters} from the {@link #eventBus}.
     *     After unregistering all presenters, it sets the {@link #eventBus} to null.
     * </p>
     *
     * @implNote This method does not check whether the field eventBus is null.
     *           The field is cleared by setting it to null.
     * @see org.greenrobot.eventbus.EventBus#unregister(Object)
     * @since 2024-09-15
     */
    public void clearEventBus(){
        if (this.eventBus != null) {
            this.eventBus.unregister(this);
            this.eventBus = null;
        }

        for (final AbstractPresenter presenter : associatedPresenters) {
            presenter.clearEventBus();
        }
    }
}
