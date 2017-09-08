package com.kecson.test;

import com.kecson.QRCodeTool;

import org.junit.Test;

public class QRcodeTest {
    @Test
    public void createQRcode() throws Exception {
        String[] args = new String[]{"-e", "-text=www.github.com/kecson/JavaProject", "-file=qr.jpg", "-dir=./"};
        QRCodeTool.main(args);
    }

    @Test
    public void createQRcodeWithLogo() throws Exception {
        String[] args = new String[]{"-e", "-text=www.github.com/kecson/JavaProject", "-file=qr-logo.jpg", "-dir=.", "-logo=H:\\studioProject\\KotlinApp\\app\\src\\main\\res\\mipmap-xxxhdpi\\ic_launcher_round.png"};
        QRCodeTool.main(args);
    }

    @Test
    public void decode() throws Exception {
        String[] args = new String[]{"-d", "H:\\studioProject\\JavaProject\\qrcodetool\\qr-logo.jpg"};
        QRCodeTool.main(args);
    }

    @Test
    public void help() throws Exception {
        String[] args = new String[]{"-h"};
        QRCodeTool.main(args);
    }

}