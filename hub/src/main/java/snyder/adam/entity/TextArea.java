/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flame on 11/30/15.
 */
public class TextArea extends Text {

    private int width;
    private String[] lines;
    private String[] words;
    private int alignment = -1; // -1: Left, 0: Center, 1: Right

    public TextArea(String text, int x, int y, int width, int size, Color color) {
        super(text, x, y, size, color);
        this.width = width;
        this.lines = new String[0];
        setText(text);
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        font.setSize(size);
        for (int i = 0; i < lines.length; i++) {
            int offset = alignment < 0 ? 0 : alignment > 0 ? width - font.getWidth(lines[i]) :
                    (width - font.getWidth(lines[i]))/2;
            font.drawString(offset + x, i*font.getLineHeight() + y, lines[i], color);
        }
    }

    public void setWidth(int width) {
        this.width = width;
        updateLines();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.words = text.split(" ");
        updateLines();
    }

    private void updateLines() {
        font.setSize(size);
        int spaceWidth = font.getWidth(" ");
        if (spaceWidth > width) {
            return;
        }
        int i = 0;
        int prevI = -1;
        List<String> lines = new ArrayList<>();
        while (i < words.length) {
            int lineWidth = 0;
            if (i == prevI) return; //TODO: Split characters by line? Or not.
            prevI = i;
            List<String> wordsInLine = new ArrayList<>();
            while (i < words.length && lineWidth + font.getWidth(words[i]) < width) {
                wordsInLine.add(words[i]);
                lineWidth += font.getWidth(words[i]) + spaceWidth;
                i++;
            }
            lines.add(String.join(" ", wordsInLine));
        }
        this.lines = new String[lines.size()];
        for (int j = 0; j < this.lines.length; j++) {
            this.lines[j] = lines.get(j);
        }
    }

    public void alignCenter() {
        alignment = 0;
    }

    public void alignLeft() {
        alignment = -1;
    }

    public void alignRight() {
        alignment = 1;
    }
}
