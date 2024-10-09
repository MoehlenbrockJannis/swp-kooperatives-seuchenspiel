package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.Optional;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @see de.uol.swp.common.message.Message
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@EqualsAndHashCode
@Setter
public abstract class AbstractMessage implements Message {

    private transient MessageContext messageContext;
    private transient Session session = null;

	@Override
	public Optional<MessageContext> getMessageContext() {
		return messageContext != null ? Optional.of(messageContext) : Optional.empty();
	}

	@Override
	public Optional<Session> getSession(){
		return session != null ? Optional.of(session) : Optional.empty();
	}

	@Override
	public void initWithMessage(final Message otherMessage) {
		otherMessage.getMessageContext().ifPresent(this::setMessageContext);
		otherMessage.getSession().ifPresent(this::setSession);
	}
}
