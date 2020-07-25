/*
 * Copyright 2020 White Magic Software, Ltd.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.whitemagicsoftware.kmcaster.listeners;

import com.whitemagicsoftware.kmcaster.HardwareSwitch;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareState.BOOLEAN_FALSE;
import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static java.lang.Math.max;
import static java.util.Map.entry;
import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;

/**
 * Responsible for sending property change events for keyboard state changes.
 */
public final class KeyboardListener
    extends PropertyDispatcher<HardwareSwitch>
    implements NativeKeyListener {
  private final static String KEY_SPACE = "Space";
  private final static String KEY_BACKSPACE = "Back ⌫";
  private final static String KEY_TAB = "Tab ↹";
  private final static String KEY_ENTER = "Enter ⏎";

  /**
   * The key is the raw key code return from the {@link NativeKeyEvent}, the
   * value is the human-readable text to display on screen.
   */
  @SuppressWarnings("JavacQuirks")
  private final static Map<Integer, String> KEY_CODES =
      Map.ofEntries(
          entry( 32, KEY_SPACE ),
          entry( 33, "!" ),
          entry( 34, "\"" ),
          entry( 35, "#" ),
          entry( 36, "$" ),
          entry( 37, "%" ),
          entry( 38, "&" ),
          entry( 39, "'" ),
          entry( 40, "(" ),
          entry( 41, ")" ),
          entry( 42, "*" ),
          entry( 43, "+" ),
          entry( 44, "," ),
          entry( 45, "-" ),
          entry( 46, "." ),
          entry( 47, "/" ),
          entry( 58, ":" ),
          entry( 59, ";" ),
          entry( 60, "<" ),
          entry( 61, "=" ),
          entry( 62, ">" ),
          entry( 63, "?" ),
          entry( 64, "@" ),
          entry( 91, "[" ),
          entry( 92, "\\" ),
          entry( 93, "]" ),
          entry( 94, "^" ),
          entry( 95, "_" ),
          entry( 96, "`" ),
          entry( 97, "a" ),
          entry( 98, "b" ),
          entry( 99, "c" ),
          entry( 100, "d" ),
          entry( 101, "e" ),
          entry( 102, "f" ),
          entry( 103, "g" ),
          entry( 104, "h" ),
          entry( 105, "i" ),
          entry( 106, "j" ),
          entry( 107, "k" ),
          entry( 108, "l" ),
          entry( 109, "m" ),
          entry( 110, "n" ),
          entry( 111, "o" ),
          entry( 112, "p" ),
          entry( 113, "q" ),
          entry( 114, "r" ),
          entry( 115, "s" ),
          entry( 116, "t" ),
          entry( 117, "u" ),
          entry( 118, "v" ),
          entry( 119, "w" ),
          entry( 120, "x" ),
          entry( 121, "y" ),
          entry( 122, "z" ),
          entry( 123, "{" ),
          entry( 124, "|" ),
          entry( 125, "}" ),
          entry( 126, "~" ),
          entry( 65056, KEY_TAB ),
          entry( 65289, KEY_TAB ),
          entry( 65293, KEY_ENTER ),
          entry( 65288, KEY_BACKSPACE ),
          entry( 65301, "SysRq" ),
          entry( 65377, "Print" ),
          entry( 65361, "←" ),
          entry( 65362, "↑" ),
          entry( 65363, "→" ),
          entry( 65364, "↓" ),
          entry( 65307, "Esc" ),
          entry( 65365, "PgUp" ),
          entry( 65366, "PgDn" ),
          entry( 65379, "Ins" ),
          entry( 65535, "Del" ),
          entry( 65506, "Shift" ),
          entry( 65407, "Num" ),
          entry( 65421, "Num ⏎" ),
          entry( 65430, "Num ←" ),
          entry( 65431, "Num ↑" ),
          entry( 65432, "Num →" ),
          entry( 65433, "Num ↓" ),
          entry( 65429, "Num Home" ),
          entry( 65434, "Num PgUp" ),
          entry( 65435, "Num PgDn" ),
          entry( 65436, "Num End" ),
          entry( 65437, "Num Clear" ),
          entry( 65438, "Num Ins" ),
          entry( 65439, "Num Del" ),
          entry( 65450, "Num *" ),
          entry( 65451, "Num +" ),
          entry( 65452, "Num Sep" ),
          entry( 65453, "Num -" ),
          entry( 65454, "Num ." ),
          entry( 65455, "Num /" ),
          entry( 65456, "Num 0" ),
          entry( 65457, "Num 1" ),
          entry( 65458, "Num 2" ),
          entry( 65459, "Num 3" ),
          entry( 65460, "Num 4" ),
          entry( 65461, "Num 5" ),
          entry( 65462, "Num 6" ),
          entry( 65463, "Num 7" ),
          entry( 65464, "Num 8" ),
          entry( 65465, "Num 9" ),
          entry( 65300, "Scrl" ),
          entry( 65509, "Caps" )
      );

  /**
   * Whether a modifier key state is pressed or released depends on the state
   * of multiple keys (left and right). This map assigns the left and right
   * key codes to the same modifier key so that the physical state can be
   * represented by a single on-screen button (the logical state).
   * <p>
   * The 65511, 65512 are shifted alt key codes.
   * </p>
   */
  private final Map<Integer, HardwareSwitch> mModifierCodes =
      Map.ofEntries(
          entry( 65505, KEY_SHIFT ),
          entry( 65506, KEY_SHIFT ),
          entry( 65507, KEY_CTRL ),
          entry( 65508, KEY_CTRL ),
          entry( 65511, KEY_ALT ),
          entry( 65512, KEY_ALT ),
          entry( 65513, KEY_ALT ),
          entry( 65514, KEY_ALT )
      );

  /**
   * Stores the state of modifier keys. The contents of the map reflect the
   * state of each switch, so the reference can be final but not its contents.
   */
  private final Map<HardwareSwitch, Integer> mModifiers = new HashMap<>();

  private String mRegularHeld = "";

  public KeyboardListener() {
    for( final var key : HardwareSwitch.modifierSwitches() ) {
      mModifiers.put( key, 0 );
    }
  }

  @Override
  public void nativeKeyPressed( final NativeKeyEvent e ) {
    updateRegular( mRegularHeld, getDisplayText( e ) );
    updateModifier( e, 1 );
  }

  @Override
  public void nativeKeyReleased( final NativeKeyEvent e ) {
    updateRegular( getDisplayText( e ), BOOLEAN_FALSE );
    updateModifier( e, -1 );
  }

  /**
   * Unused. Key up and key down are tracked separately from a typed key.
   *
   * @param e Ignored.
   */
  @Override
  public void nativeKeyTyped( final NativeKeyEvent e ) {
  }

  /**
   * Sets the initial state of the modifiers.
   */
  public void initModifiers() {
    for( final var key : mModifiers.keySet() ) {
      final var state = mModifiers.get( key );

      // All modifiers keys are "false" by default, so firing fake transition
      // events from "true" to "false" will cause the GUI to repaint with the
      // text label affixed to each key, drawn in the released state. This
      // happens before the frame is set to visible.
      tryFire( key, state == 0, state == 1 );
    }
  }

  /**
   * State for a regular (non-modifier) key has changed.
   *
   * @param o Previous key value.
   * @param n Current key value.
   */
  private void updateRegular( final String o, final String n ) {
    assert o != null;
    assert n != null;

    boolean isModifier = false;

    // The key is regular iff its name does not match any modifier name.
    for( final var key : mModifiers.keySet() ) {
      isModifier |= (key.isName( n ) || key.isName( o ));
    }

    // If it's not a modifier key, broadcast the regular value.
    if( !isModifier ) {
      tryFire( KEY_REGULAR, o, n );
      mRegularHeld = n;
    }
  }

  /**
   * Notifies of any modifier state changes. There's a bug whereby this
   * method is never called by the native library when both Left/Right Ctrl
   * keys are pressed followed by pressing either Shift key. Similarly,
   * holding both Left/Right Shift keys followed by pressing either Ctrl key
   * fails to call this method.
   *
   * @param e The keyboard event that was most recently triggered.
   */
  private void updateModifier( final NativeKeyEvent e, final int increment ) {
    final var key = mModifierCodes.get( e.getRawCode() );

    if( key != null ) {
      final var oldCount = mModifiers.get( key );
      final var newCount = max( oldCount + increment, 0 );

      tryFire( key, oldCount > 0, newCount > 0 );
      mModifiers.put( key, newCount );
    }
  }

  /**
   * Looks up the key code for the given event. If the key code is not mapped,
   * this will return the default value from the native implementation.
   *
   * @param e The keyboard event that was triggered.
   * @return The human-readable name for the key relating to the event.
   */
  private String getDisplayText( final NativeKeyEvent e ) {
    return KEY_CODES.getOrDefault(
        e.getRawCode(), getKeyText( e.getKeyCode() )
    );
  }
}
