package ben.ui.widget;

import ben.ui.action.IAction;
import ben.ui.input.mouse.MouseButton;
import ben.ui.input.mouse.MouseListenerAdapter;
import ben.ui.math.PmvMatrix;
import ben.ui.math.Rect;
import ben.ui.math.Vec2i;
import ben.ui.resource.GlResourceManager;
import ben.ui.resource.color.Color;
import ben.ui.renderer.FlatRenderer;
import ben.ui.renderer.TextRenderer;
import javax.annotation.Nonnull;

import com.jogamp.opengl.GL2;
import javax.annotation.Nullable;

/**
 * Button.
 */
public final class Button extends AbstractWidget {

    /**
     * The padding around the button.
     */
    private static final int PADDING = 5;

    /**
     * The border size in pixels.
     */
    private static final int BORDER = 1;

    /**
     * The height of the button.
     */
    private static final int HEIGHT = TextRenderer.CHARACTER_SIZE + 2 * PADDING;

    /**
     * The background colour of the button.
     */
    @Nonnull
    private static final Color BACKGROUND_COLOR = new Color(0.31f, 0.34f, 0.35f);

    /**
     * The border colour of the button.
     */
    @Nonnull
    private static final Color BORDER_COLOR = new Color(0.37f, 0.38f, 0.38f);

    /**
     * The highlighted border colour of the button.
     */
    @Nonnull
    private static final Color HIGHLIGHTED_BORDER_COLOR = new Color(0.42f, 0.65f, 0.87f);

    /**
     * The text colour.
     */
    @Nonnull
    private static final Color TEXT_COLOR = new Color(0.73f, 0.73f, 0.73f);

    /**
     * The disabled text colour.
     */
    @Nonnull
    private static final Color DISABLED_TEXT_COLOR = new Color(0.5f, 0.5f, 0.5f);

    /**
     * The text.
     */
    @Nonnull
    private final String text;

    /**
     * The background renderer.
     */
    @Nullable
    private FlatRenderer backgroundRenderer;

    /**
     * The border renderer.
     */
    @Nullable
    private FlatRenderer borderRenderer;

    /**
     * The text renderer.
     */
    @Nullable
    private TextRenderer textRenderer;

    /**
     * Constructor.
     * @param name the name of the button
     * @param text the text
     */
    public Button(@Nullable String name, @Nonnull String text) {
        super(name);
        this.text = text;
        getMouseHandler().addMouseListener(new MouseListener());
        setSize(getPreferredSize());
    }

    @Override
    public String toString() {
        return Button.class.getSimpleName() + "[text: '" + text + "']";
    }

    @Override
    protected void initDraw(@Nonnull GL2 gl, @Nonnull GlResourceManager glResourceManager) {
        backgroundRenderer = new FlatRenderer(gl, glResourceManager, getBgRect(), BACKGROUND_COLOR);
        borderRenderer = new FlatRenderer(gl, glResourceManager, getBorderRect(), BORDER_COLOR);
        textRenderer = new TextRenderer(gl, glResourceManager, text, new Vec2i(PADDING, PADDING), TEXT_COLOR);
    }

    @Override
    protected void updateDraw(@Nonnull GL2 gl) {
        assert backgroundRenderer != null : "Update draw should not be called before init draw";
        assert borderRenderer != null : "Update draw should not be called before init draw";
        assert textRenderer != null : "Update draw should not be called before init draw";

        backgroundRenderer.setRect(gl, getBgRect());
        borderRenderer.setRect(gl, getBorderRect());
        textRenderer.setText(gl, text);
    }

    /**
     * Get the background position and size.
     * @return the rectangle
     */
    private Rect getBgRect() {
        Vec2i bgPos = new Vec2i(BORDER, BORDER);
        Vec2i bgSize = getSize().sub(new Vec2i(2 * BORDER, 2 * BORDER));
        return new Rect(bgPos, bgSize);
    }

    /**
     * Get the border position and size.
     * @return the rectangle
     */
    private Rect getBorderRect() {
        Vec2i borderPos = new Vec2i(0, 0);
        Vec2i borderSize = getSize();
        return new Rect(borderPos, borderSize);
    }

    @Override
    protected void doDraw(@Nonnull GL2 gl, @Nonnull PmvMatrix pmvMatrix) {
        assert borderRenderer != null : "Draw should not be called before init draw";
        assert backgroundRenderer != null : "Draw should not be called before init draw";
        assert textRenderer != null : "Draw should not be called before init draw";

        borderRenderer.draw(gl, pmvMatrix);
        backgroundRenderer.draw(gl, pmvMatrix);

        textRenderer.setColor(isEnabled() ? TEXT_COLOR : DISABLED_TEXT_COLOR);
        textRenderer.draw(gl, pmvMatrix);
    }

    @Nonnull
    @Override
    public Vec2i getPreferredSize() {
        int width = text.length() * TextRenderer.CHARACTER_SIZE + 2 * PADDING;
        return new Vec2i(width, HEIGHT);
    }

    @Override
    protected void preRemove(@Nonnull GL2 gl) {
        if (borderRenderer != null) {
            borderRenderer.remove(gl);
        }
        if (backgroundRenderer != null) {
            backgroundRenderer.remove(gl);
        }
        if (textRenderer != null) {
            textRenderer.remove(gl);
        }
    }

    /**
     * The Mouse Listener.
     * <p>
     *     Highlights the button on mouse over and executes the action when the button is clicked.
     * </p>
     */
    private class MouseListener extends MouseListenerAdapter {

        @Override
        public void mouseEntered() {
            if (isEnabled() && borderRenderer != null) {
                borderRenderer.setColor(HIGHLIGHTED_BORDER_COLOR);
            }
        }

        @Override
        public void mouseExited() {
            if (borderRenderer != null) {
                borderRenderer.setColor(BORDER_COLOR);
            }
        }

        @Override
        public void mouseClicked(@Nonnull MouseButton button) {
            IAction action = getAction();
            if (action != null) {
                action.execute();
            }
        }
    }
}
