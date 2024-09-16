package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.user.ClientUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the base for creating a new Presenter.
 *
 * This class prepares the child classes to have the UserService and EventBus set
 * in order to reduce unnecessary code repetition.
 *
 * @author Marco Grawunder
 * @since 2019-08-29
 */


public class AbstractPresenter {

    private static final Logger LOG = LogManager.getLogger(AbstractPresenter.class);

    @Inject
    protected ClientUserService userService;

    protected EventBus eventBus;

    protected List<AbstractPresenter> associatedPresenters = new ArrayList<>();

    /**
     * Sets the field eventBus
     *
     * This method sets the field eventBus to the EventBus given via parameter.
     * Afterwards it registers this class to the new EventBus.
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
        this.eventBus.unregister(this);
        for (final AbstractPresenter presenter : associatedPresenters) {
            presenter.clearEventBus();
        }
        this.eventBus = null;
    }
}
