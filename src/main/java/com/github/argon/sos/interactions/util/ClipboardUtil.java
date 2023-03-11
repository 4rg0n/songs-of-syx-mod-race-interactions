package com.github.argon.sos.interactions.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

public class ClipboardUtil {
    public static void write(String string) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(string);

        clipboard.setContents(stringSelection, stringSelection);
    }

    public static Optional<String> read() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = clipboard.getContents( null );

        if (!transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return Optional.empty();
        }

        try {
            String text = (String) transfer.getTransferData( DataFlavor.stringFlavor );
            return Optional.of(text);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
