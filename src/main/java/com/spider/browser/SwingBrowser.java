package com.spider.browser;

import com.spider.config.ProxyConstant;
import com.spider.proxy.ProxyBean;
import com.spider.proxy.impl.ProxyRemoteManage;
import com.spider.engine.htmlunit.WebClientPool;
import com.spider.proxy.ProxyManage;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static javafx.concurrent.Worker.State.FAILED;

public class SwingBrowser extends JFrame {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final static SwingBrowser BROWSER = new SwingBrowser();
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();


    private final JButton btnGo = new JButton("Go");
    private final JTextField txtURL = new JTextField();

    private final JButton btnJs = new JButton("run js");
    private final JTextField txtJs = new JTextField();

    private final JCheckBox jsEnable = new JCheckBox("");


    private final WebClientPool pool = WebClientPool.getInstance();

    private HtmlPage rootPage = null;

    private WebClient webClient = null;

    private ProxyManage proxyManage = new ProxyRemoteManage();


    public SwingBrowser() {
        super();
        initComponents();
    }


    private void initComponents() {
        createScene();

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadURL(txtURL.getText());
            }
        };

        ActionListener runjs = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadJs(txtJs.getText());
            }
        };

        btnGo.addActionListener(al);
        txtURL.addActionListener(al);

        btnJs.addActionListener(runjs);


        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(txtURL, BorderLayout.CENTER);
        topBar.add(btnGo, BorderLayout.EAST);
        topBar.add(jsEnable, BorderLayout.WEST);

        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(txtJs, BorderLayout.CENTER);
        statusBar.add(btnJs, BorderLayout.EAST);


        panel.add(topBar, BorderLayout.NORTH);
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(panel);

        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

    }

    private void createScene() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                WebView view = new WebView();
                engine = view.getEngine();

                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                SwingBrowser.this.setTitle(newValue);
                            }
                        });
                    }
                });

                engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override
                    public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });

                engine.locationProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                txtURL.setText(newValue);
                            }
                        });
                    }
                });

                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {

                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            JOptionPane.showMessageDialog(
                                                    panel,
                                                    (value != null) ?
                                                            engine.getLocation() + "\n" + value.getMessage() :
                                                            engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });

                jfxPanel.setScene(new Scene(view));
            }
        });
    }

    public void loadURL(final String url) {
        if (webClient != null) {
            webClient.close();
            rootPage = null;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                String tmp = toURL(url);
//
//                if (tmp == null) {
//                    tmp = toURL("http://" + url);
//                }
//
//                engine.load(tmp);
                try {
                    if (webClient == null) {
                        webClient = pool.getClient();
                        webClient.addRequestHeader("Accept-Encoding", "deflate, sdch");
                        webClient.getCookieManager().setCookiesEnabled(true);
                    }
                    if (rootPage == null) {
                        WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(url));
                        webRequest.setCharset("UTF-8");
                        if (ProxyConstant.PROXY_SWITCH == 1) {
                            ProxyBean proxyBean = proxyManage.getProxyBean();
                            webRequest.setProxyHost(proxyBean.getIp());
                            webRequest.setProxyPort(proxyBean.getPort());
                        }
                        webClient.getOptions().setJavaScriptEnabled(jsEnable.isSelected());
                        rootPage = webClient.getPage(webRequest);
                        if (jsEnable.isSelected()) {
                            TimeUnit.SECONDS.sleep(5);
                        }
                        loadContent(rootPage.asXml());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    public void loadJs(final String js) {
        try {
            if (webClient == null) {
                return;
            }
            webClient.getOptions().setJavaScriptEnabled(true);
            ScriptResult scriptResult = rootPage.executeJavaScript(js);
            String msg = "执行错误";
            if (scriptResult != null && scriptResult.getJavaScriptResult() != null) {
                msg = scriptResult.getJavaScriptResult().toString();
            } else {
                LOG.error(rootPage.asXml());
            }
            loadContent(msg);
//            JOptionPane.showMessageDialog(null, msg, "js执行结果如下：", JOptionPane.INFORMATION_MESSAGE);
            rootPage.cleanUp();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void loadContent(final String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.loadContent(content);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

    /**
     * 打开窗口展示html内容
     */
    public static void showContent(final String url, final String content) {
        BROWSER.setVisible(true);
        BROWSER.txtURL.setText(url);
        BROWSER.loadContent(content);
    }


    public static void main(String[] args) {
        showContent("", "<h1>welcome !!!</h1>");
    }

}