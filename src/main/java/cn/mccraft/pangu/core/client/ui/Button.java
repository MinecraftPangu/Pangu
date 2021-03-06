package cn.mccraft.pangu.core.client.ui;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

@Accessors(chain = true)
public abstract class Button extends Component {
    /**
     * Enable press sound (UI_BUTTON_CLICK)
     */
    @Setter
    @Getter
    private boolean isPlayPressSound = true;

    /**
     * Close screen when button clicked
     */
    @Setter
    @Getter
    private boolean clickToClose = false;

    @Getter
    @Setter
    @Deprecated
    protected ClickEvent clickEvent;

    @Getter
    protected Consumer<ButtonClickEvent> buttonClickEvent;

    public Button() {
        super();
    }

    public Button(float width, float height) {
        this();
        setSize(width, height);
    }

    @Deprecated
    public Button(int width, int height) {
        this((float) width, height);
    }

    public void onClick(int mouseButton, int mouseX, int mouseY) {
        if (isDisabled()) return;
        if (isPlayPressSound()) playPressSound();
        if (getButtonClickEvent() != null) getButtonClickEvent().accept(new ButtonClickEvent(this, mouseButton, mouseX, mouseY));
        if (getClickEvent() != null) getClickEvent().onClick(mouseButton, mouseX, mouseY);
        if (isClickToClose() && getScreen() != null) getScreen().closeScreen();
    }

    @Override
    public void onMousePressed(int mouseButton, int mouseX, int mouseY) {
        if (!actionOnButton(mouseButton, mouseX, mouseY)) return;
        if (actionOnReleased(mouseButton, mouseX, mouseY)) return;
        this.onClick(mouseButton, mouseX, mouseY);
    }

    @Override
    public void onMouseReleased(int mouseButton, int mouseX, int mouseY) {
        if (!actionOnButton(mouseButton, mouseX, mouseY)) return;
        if (!actionOnReleased(mouseButton, mouseX, mouseY)) return;
        this.onClick(mouseButton, mouseX, mouseY);
    }

    /**
     * @return if action click event while mouse released
     */
    public boolean actionOnReleased(int mouseButton, int mouseX, int mouseY) {
        return true;
    }

    /**
     * @return if action click event while mouse released
     */
    public boolean actionOnButton(int mouseButton, int mouseX, int mouseY) {
        return mouseButton == 0;
    }

    /**
     * @return 0 = disabled, 1 = normal, 2 = hovered
     */
    public int getHoverState() {
        int i = 1;
        if (isDisabled()) i = 0;
        else if (isHovered()) i = 2;
        return i;
    }

    @FunctionalInterface
    public interface ClickEvent {
        void onClick(int mouseButton, int mouseX, int mouseY);
    }

    public Button onButtonClick(Consumer<ButtonClickEvent> consumer) {
        buttonClickEvent = consumer;
        return this;
    }

    @Data
    public static class ButtonClickEvent {
        private final Button button;
        private final int mouseButton, mouseX, mouseY;
    }
}
