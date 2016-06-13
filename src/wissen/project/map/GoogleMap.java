package wissen.project.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;

/**
 * Date 25.04.2016 <br/>
 *
 * @author Ömer İlhanlı
 */
public class GoogleMap extends javax.swing.JFrame {

    /**
     * Field <br/><br/>
     * Adres satırı.
     */
    String adr = "https://maps.googleapis.com/maps/api/staticmap?";

    /**
     * Field <br/><br/>
     * Geçici adres satırı. ('adr'nin ilk haline donmesi için.)
     */
    String adrTmp = "https://maps.googleapis.com/maps/api/staticmap?";

    /**
     * Field <br/><br/>
     * Geçici tutulan Label texti.
     */
    String lblText = "";

    /**
     * Field <br/><br/>
     * Label her yeniden boyutlandırıldığında, resmin otomatik yeniden
     * boyutlandırılması için tutulan değer.
     */
    boolean resGoster;

    /**
     * Field<br/><br/>
     * Internet olmadığında Label içinde offline bilgisi vermeyi sağlayan
     * boolean değişken.
     */
    boolean offline;

    /**
     * Field <br/><br/>
     * Label'e yuklenmesi için tutulan ImageIcon değeri.
     */
    ImageIcon imgIcon;

    public GoogleMap() {
        initComponents();
        // Application Adı.
        setTitle("GoogleMap Static Api Controller");
        setLocationRelativeTo(null);

        // lblZoom'un İlk Adi Geçici lblText Değişkeninde Tutulur.
        lblText = lblZoom.getText();

        // lblZoom Adı Yanında İlk Açılınca Zoom'daki Default Değer Bilgisi Yazılır.
        lblZoom.setText(lblZoom.getText() + zoom.getValue());

        // İlk Açılışta Harita Türü Olarak Road Map Seçili Olur.
        roadmap.setSelected(true);

        // Tüm Butonlar Gruba Alınır. Biri Seçilince Ötekisi Pasif Olur.
        ButtonGroup group = new ButtonGroup();
        group.add(roadmap);
        group.add(terrain);
        group.add(satellite);

        // -- Internet olunca, goster buton yeşil, olmadığında kırmızı renkte görünümü verir.
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL("http://www.google.com.tr");
                    URLConnection urlC = url.openConnection();
                    // Standart Bir Adres İle Timer Task İçinde Olarak İnternete
                    // Bağlanılıp Bağlanmadığını Kontrol İçin URLConnection
                    // Açıp Gelen Değerin Null Olup-Olmaması
                    // İle Buton Rengi Kırmızıdan Yeşile Döner.

                    // Yeşil: Online, Kırmızı: Offline
                    if (urlC.getHeaderFieldKey(1) != null) {
                        offline = false;
                        if (!offline) {
                            // Offline Değilse, Yeşil
                            goster.setForeground(new Color(79, 195, 128));
                        }
                    } else {
                        offline = true;
                        if (offline) {
                            // Offline ise Kırmızı
                            goster.setForeground(new Color(182, 0, 0));
                        }
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        timer.schedule(task, 0, 1000);

        // Her Yeniden BOyutlandırmayla Resmin Boyutları Yeniden Ayarlanır.
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                System.out.println("RESİZE edildi.");
                System.out.println("new Label Height : " + lbl.getWidth());
                System.out.println("new Label Width : " + lbl.getHeight());

                if (resGoster) {

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(adr);
                                imgIcon = new ImageIcon(url);
                                Image img = fitImage(imgIcon.getImage(), lbl.getWidth(), lbl.getHeight());

                                int lblBoy = lbl.getWidth(), lblEn = lbl.getHeight();
                                System.out.println("Label Boyu : " + lblBoy);
                                System.out.println("Label Eni : " + lblEn);

                                lbl.setIcon(new ImageIcon(img));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * Method<br/><br/>
     * Girilen Yeni Parametreler 'adr' Link Adresinin Sonuna Eklenmek Üzere
     * Yeniden Oluşturulur.
     */
    public void fitAdresParam() {

        // Adreste Boşluk veya Boşlu+Virgül Girilirse Otomatik Virgüle Çevirilir
        // Bu Şekilde Google Api Bunu Direkt Anlayarak Haritayı Gönderir.
        String adresUygun = adres.getText();
        if (adresUygun.contains(" ")) {
            adresUygun = adresUygun.replace(" ", ",");
        }

        // Adres Parametre Kısmı
        String adrParam = "center=Istanbul,Yesilkoy&zoom=15&size=450x300&maptype=satellite";

        // Gui'de Yazılan Değerler Parametre Kısma Eklenir.
        String[] paramParcalalar = adrParam.split("&");
        String paramTmp = "";
        paramTmp += paramParcalalar[0].replace(paramParcalalar[0].substring(paramParcalalar[0].indexOf("=") + 1), adresUygun) + "&";
        paramTmp += paramParcalalar[1].replace(paramParcalalar[1].substring(paramParcalalar[1].indexOf("=") + 1), "" + zoom.getValue()) + "&";
        paramTmp += paramParcalalar[2].replace(paramParcalalar[2].substring(paramParcalalar[2].indexOf("=") + 1), yatay.getText() + "x" + dikey.getText()) + "&";
        if (roadmap.isSelected()) {
            paramTmp += paramParcalalar[3].replace(paramParcalalar[3].substring(paramParcalalar[3].indexOf("=") + 1), "roadmap");
        } else if (terrain.isSelected()) {
            paramTmp += paramParcalalar[3].replace(paramParcalalar[3].substring(paramParcalalar[3].indexOf("=") + 1), "terrain");
        } else if (satellite.isSelected()) {
            paramTmp += paramParcalalar[3].replace(paramParcalalar[3].substring(paramParcalalar[3].indexOf("=") + 1), "satellite");
        }
        //----            'adr' Adresi Yeniden Oluşturulur.         ----\\
        adr += paramTmp;
    }

    /**
     * Method <br/><br/>
     * Label Her Büyüyüp Küçüldüğünde Resim Yeniden Boyutlandırılır.
     * <br/><br/>
     *
     * @param img
     * @param w
     * @param h
     * @return
     */
    private Image fitImage(Image img, int w, int h) {
        //---- Alınan resim BufferedImage'a istenen boyutla alınır.
        BufferedImage resizedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //---- Grapics2D ile resmin iki boyutlu hali BufferedImage instance'ından alınıp oluşturulur.
        Graphics2D g2 = resizedimage.createGraphics();

        //--- Resim Render edilir //
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //--- Resim yeniden çizilir //
        g2.drawImage(img, 0, 0, w, h, null);
        g2.setColor(Color.YELLOW);
        g2.dispose();
        return resizedimage;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        adres = new javax.swing.JTextField();
        lblZoom = new javax.swing.JLabel();
        zoom = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        yatay = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        dikey = new javax.swing.JTextField();
        goster = new javax.swing.JButton();
        lbl = new javax.swing.JLabel();
        roadmap = new javax.swing.JRadioButton();
        terrain = new javax.swing.JRadioButton();
        satellite = new javax.swing.JRadioButton();
        reset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(0, 0));

        jLabel1.setFont(new java.awt.Font("Superclarendon", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(" Adres Veya Koordinat Bilgisi");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblZoom.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        lblZoom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblZoom.setText(" Yakınlaştırma Seviyesi ");
        lblZoom.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        zoom.setMaximum(20);
        zoom.setMinimum(1);
        zoom.setValue(14);
        zoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("1");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("20");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel5.setFont(new java.awt.Font("Superclarendon", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText(" Harita Türü");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel6.setFont(new java.awt.Font("Chalkduster", 0, 14)); // NOI18N
        jLabel6.setText("Road Map");

        jLabel61.setFont(new java.awt.Font("Chalkduster", 0, 14)); // NOI18N
        jLabel61.setText("Terrain");

        jLabel62.setFont(new java.awt.Font("Chalkduster", 0, 14)); // NOI18N
        jLabel62.setText("Satellite");

        jLabel9.setFont(new java.awt.Font("Superclarendon", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText(" Resim Boyutu");
        jLabel9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        yatay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yatay.setText("500");
        yatay.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 204, 153), null));

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("x");
        jLabel10.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        dikey.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dikey.setText("500");
        dikey.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 204, 153), null));

        goster.setFont(new java.awt.Font("Superclarendon", 1, 18)); // NOI18N
        goster.setText("Göster");
        goster.setToolTipText("green=online; red=offline");
        goster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gosterActionPerformed(evt);
            }
        });

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 51, 51))); // NOI18N
        lbl.setMinimumSize(new java.awt.Dimension(1, 1));
        lbl.setOpaque(true);

        roadmap.setOpaque(true);

        terrain.setOpaque(true);

        satellite.setOpaque(true);

        reset.setFont(new java.awt.Font("Superclarendon", 0, 14)); // NOI18N
        reset.setText("Reset Values");
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(zoom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(yatay, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dikey, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(goster, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(roadmap, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(terrain)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(satellite)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(adres, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(reset, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(lblZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {roadmap, satellite, terrain});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reset, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adres, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblZoom, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zoom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(satellite, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(terrain, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(roadmap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(yatay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dikey, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(goster, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {roadmap, satellite, terrain});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action Perform Event <br/><br/>
     * Goster Butonu Her Tıklandığında, Resim İnternetten Yeniden İndirilir.
     * <br/><br/>
     *
     * @param evt
     */
    private void gosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gosterActionPerformed
        //------ Buton Her Tıklandığında 'adr' Parametresiz Hale Döner.
        adr = adrTmp;

        //----- Girilen Parametreler Oluşturulur ve 'adr' Yeniden Oluşturulur.
        fitAdresParam();

        //-------------------** Yeni Bir Thread Açılır ve İnternetten İnen Resim Label'e Yüklenir.
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!offline) {
                        URL url = new URL(adr);
                        lbl.setForeground(null);
                        ImageIcon ic = new ImageIcon(url);
                        Image img = fitImage(ic.getImage(), lbl.getWidth(), lbl.getHeight());
                        lbl.setIcon(new ImageIcon(img));
                        resGoster = true;
                    } else {
                        lbl.setIcon(null);
                        lbl.setText("Internet bağlantınız kesik. Lütfen internete bağlanıp yeniden deneyin...");
                        lbl.setBackground(Color.cyan);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }//GEN-LAST:event_gosterActionPerformed

    /**
     * State Changed Event <br/><br/>
     * Zoom Slider'ının Buton Değeri Her Değiştiğinde Slider Adının Yanında
     * Değer Bilgisi Gelir.
     * <br/><br/>
     *
     * @param evt
     */
    private void zoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomStateChanged
        // Zoom Butonu Her Oynadığında Zoom Adının Yanında Zoom Değeri Yazılır.
        String tmp1 = lblText;
        lblZoom.setText("");
        lblZoom.setText(tmp1 + zoom.getValue());
    }//GEN-LAST:event_zoomStateChanged

    /**
     * ActionPerform Event <br/><br/>
     * Butona Basıldığında Tüm Değerler İlk Haline Döner.
     *
     * @param evt
     */
    private void resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetActionPerformed
        adres.setText("");
        zoom.setValue(10);
        roadmap.setSelected(true);
        terrain.setSelected(false);
        satellite.setSelected(false);

        yatay.setText("");
        dikey.setText("");
    }//GEN-LAST:event_resetActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GoogleMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GoogleMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GoogleMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GoogleMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GoogleMap().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adres;
    private javax.swing.JTextField dikey;
    private javax.swing.JButton goster;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lbl;
    private javax.swing.JLabel lblZoom;
    private javax.swing.JButton reset;
    private javax.swing.JRadioButton roadmap;
    private javax.swing.JRadioButton satellite;
    private javax.swing.JRadioButton terrain;
    private javax.swing.JTextField yatay;
    private javax.swing.JSlider zoom;
    // End of variables declaration//GEN-END:variables

}
