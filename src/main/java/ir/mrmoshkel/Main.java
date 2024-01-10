package ir.mrmoshkel;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;


public class Main {
    static WebElement inputField, clearBtn;
    static boolean isCtrlPressed = false;

    public static void main(String[] args) throws NativeHookException {

        GlobalScreen.registerNativeHook();
        System.setProperty("webdriver.chrome.driver", args[0]);
        WebDriver driver = new ChromeDriver();
        driver.get("https://translate.google.com/?sl=en&tl=fa&op=translate");
        inputField = driver.findElement(By.cssSelector("textarea[jsname='BJE2fc']"));
        clearBtn = driver.findElement(By.className("DVHrxd"));

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL)
                    isCtrlPressed = true;
                else if (isCtrlPressed == true && e.getKeyCode() == NativeKeyEvent.VC_C) {
                    try {
                        clearBtn.click();
                        inputField.sendKeys(extractDataFromClipboard());
                    } catch (Exception ex) {
                        //When clear button hidden
                        inputField.sendKeys(extractDataFromClipboard());
                    }
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyListener.super.nativeKeyReleased(nativeEvent);
            }
        });

//        String clipContent = "";
//        String extracted = "";
//        while (true) {
//            try {
//                Thread.sleep(1000);
//                extracted = extractDataFromClipboard();
//                if (!clipContent.equals(extracted)) {
//                    clipContent = extracted;
//                    try {
//                        clearBtn.click();
//                        inputField.sendKeys(clipContent);
//                    } catch (Exception ex) {
//                        //When clear button hidden
//                        inputField.sendKeys(clipContent);
//                    }
//                }
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    private static String extractDataFromClipboard() {
        String clipboardText = "";
        // Get the system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipboardData = clipboard.getContents(null);
        if (clipboardData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                // Retrieve and print the data from the clipboard
                clipboardText = (String) clipboardData.getTransferData(DataFlavor.stringFlavor);
                clipboardText.chars().parallel()
                        .map(ch -> ch == '\n' ? ' ' : ch)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString();
                return clipboardText.replaceAll("\\n|\\r", " ");
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return clipboardText;
    }
}