package org.jboss.errai.bus.server.security.auth.rules;

import org.jboss.errai.bus.client.BooleanRoutingRule;
import org.jboss.errai.bus.client.CommandMessage;
import org.jboss.errai.bus.client.MessageBus;
import org.jboss.errai.bus.client.protocols.SecurityCommands;
import org.jboss.errai.bus.client.protocols.SecurityParts;
import org.jboss.errai.bus.server.security.auth.AuthSubject;
import org.jboss.errai.bus.server.service.ErraiService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

public class RolesRequiredRule implements BooleanRoutingRule {
    private Set<Object> requiredRoles;
    private MessageBus bus;

    public RolesRequiredRule(String[] requiredRoles, MessageBus bus) {
        this.requiredRoles = new HashSet<Object>();
        for (String role : requiredRoles) {
            this.requiredRoles.add(role.trim());
        }
        this.bus = bus;
    }

    public RolesRequiredRule(Set<Object> requiredRoles, MessageBus bus) {
        this.requiredRoles = requiredRoles;
        this.bus = bus;
    }

    public boolean decision(CommandMessage message) {
        if (!message.hasPart(SecurityParts.SessionData)) return false;
        else {
            AuthSubject subject = (AuthSubject) message.get(HttpSession.class, SecurityParts.SessionData)
                    .getAttribute(ErraiService.SESSION_AUTH_DATA);

            if (subject == null) {
                /**
                 * Inform the client they must login.
                 */
                bus.send(CommandMessage.create(SecurityCommands.SecurityChallenge)
                        .toSubject("LoginClient")
                        .set(SecurityParts.CredentialsRequired, "Name,Password")
                        .set(SecurityParts.ReplyTo, ErraiService.AUTHORIZATION_SVC_SUBJECT)
                        .set(SecurityParts.SessionData, message.get(HttpSession.class, SecurityParts.SessionData))
                        , false);
                return false;
            }

            Set<Object> sessionRoles = subject.getRoles();

            return sessionRoles.containsAll(requiredRoles);
        }
    }
}
