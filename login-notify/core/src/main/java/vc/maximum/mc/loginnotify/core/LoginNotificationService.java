package vc.maximum.mc.loginnotify.core;

public final class LoginNotificationService {

  private final LoginNotifier notifier;

  public LoginNotificationService(LoginNotifier notifier) {
    this.notifier = notifier;
  }

  public void notify(ConnectionEvent event) {
    notifier.notify(event);
  }

  public String notifierId() {
    return notifier.id();
  }
}
