package konfigdb;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextField;


public class crud {

    private Connection koneksiDB;
    private String username = "root";
    private String password = "";
    private String jdbcurl = "jdbc:mysql://localhost:3306/perkantoran_2410010229";

    public crud() {
        try {
            Driver mysqlDriver = new com.mysql.jdbc.Driver();
            DriverManager.deregisterDriver(mysqlDriver);
            this.koneksiDB = DriverManager.getConnection(jdbcurl, username, password);
            System.out.print("database terkoneksi");
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    // penanda bahwa parameter ini adalah tanggal
    private static class Tgl {
        String teks;
        Tgl(String teks) { this.teks = teks; }
    }

    private java.sql.Date ubahTanggal(String teks) throws Exception {
        teks = teks.trim();
        if (teks.isEmpty()) {
            return null;
        }
        java.text.SimpleDateFormat fmt;
        if (teks.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
        } else if (teks.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            fmt = new java.text.SimpleDateFormat("dd-MM-yyyy");
        } else {
            throw new Exception("Format tanggal salah. Gunakan yyyy-mm-dd atau dd-mm-yyyy, contoh: 2026-11-04");
        }
        fmt.setLenient(false);
        return new java.sql.Date(fmt.parse(teks).getTime());
    }

    private void jalankan(String sql, String pesan, Object... param) {
        try {
            PreparedStatement ps = koneksiDB.prepareStatement(sql);
            for (int i = 0; i < param.length; i++) {
                Object p = param[i];
                if (p instanceof Tgl) {
                    java.sql.Date d = ubahTanggal(((Tgl) p).teks);
                    if (d == null) {
                        ps.setNull(i + 1, Types.DATE);
                    } else {
                        ps.setDate(i + 1, d);
                    }
                } else {
                    ps.setObject(i + 1, p);
                }
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, pesan);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // ===== SURAT MASUK =====
    // urutan: id_suratmasuk, no_surat, tanggal, perihal, asal_surat
      public void simpanSuratMasuk(String no, String tgl, String perihal, String asal) {
    jalankan("insert into surat_masuk (no_surat, tanggal, perihal, `asal surat`) values (?,?,?,?)",
            "data berhasil disimpan", no, new Tgl(tgl), perihal, asal);
}

    public void ubahSuratMasuk(String id, String no, String tgl, String perihal, String asal) {
        jalankan("update surat_masuk set no_surat=?, tanggal=?, perihal=?, `asal surat`=? where id_suratmasuk=?",
                "data berhasil diubah", no, new Tgl(tgl), perihal, asal, id);
    }

    public void hapusSuratMasuk(String id) {
        jalankan("delete from surat_masuk where id_suratmasuk=?", "data berhasil dihapus", id);
    }
    // Mencari data surat masuk berdasarkan ID
public void cariSuratMasuk(String id, JTextField no,
        JTextField tanggal, JTextField perihal, JTextField asal) {

    String sql = "SELECT * FROM surat_masuk WHERE id_suratmasuk=?";

    try {
        PreparedStatement ps = koneksiDB.prepareStatement(sql);
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            no.setText(rs.getString("no_surat"));

            java.sql.Date tgl = rs.getDate("tanggal");
            tanggal.setText(tgl == null ? "" : tgl.toString());

            perihal.setText(rs.getString("perihal"));
            asal.setText(rs.getString("asal surat"));

        } else {
            JOptionPane.showMessageDialog(null,
                    "Data surat masuk tidak ditemukan!");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
    }
}

    // ===== SURAT KELUAR =====
    // urutan: id_suratkeluar, no_surat, tanggal_kirim, tujuan, perihal
        public void simpanSuratKeluar(String no, String tgl, String tujuan, String perihal) {
    jalankan("insert into surat_keluar (no_surat, tanggal_kirim, tujuan, perihal) values (?,?,?,?)",
            "data berhasil disimpan", no, new Tgl(tgl), tujuan, perihal);
}

public void ubahSuratKeluar(String id, String no, String tgl, String tujuan, String perihal) {
    jalankan("update surat_keluar set no_surat=?, tanggal_kirim=?, tujuan=?, perihal=? where id_suratkeluar=?",
            "data berhasil diubah", no, new Tgl(tgl), tujuan, perihal, id);
}

public void hapusSuratKeluar(String id) {
    jalankan("delete from surat_keluar where id_suratkeluar=?",
            "data berhasil dihapus", id);
}
// Mencari data surat keluar berdasarkan ID
public void cariSuratKeluar(String id, JTextField no,
        JTextField tanggal, JTextField tujuan, JTextField perihal) {

    String sql = "SELECT * FROM surat_keluar WHERE id_suratkeluar=?";

    try {
        PreparedStatement ps = koneksiDB.prepareStatement(sql);
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            no.setText(rs.getString("no_surat"));

            java.sql.Date tgl = rs.getDate("tanggal_kirim");
            tanggal.setText(tgl == null ? "" : tgl.toString());

            tujuan.setText(rs.getString("tujuan"));
            perihal.setText(rs.getString("perihal"));

        } else {
            JOptionPane.showMessageDialog(null,
                    "Data surat keluar tidak ditemukan!");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
    }
}

    // ===== LAPORAN =====
    // urutan: id_suratmasuk, id_suratkeluar
    public void simpanLaporan(String idMasuk, String idKeluar) {
        jalankan("insert into tabel_laporan (id_suratmasuk, id_suratkeluar) values (?,?)",
                "data berhasil disimpan", idMasuk, idKeluar);
    }

    public void ubahLaporan(String idMasuk, String idKeluar) {
        jalankan("update tabel_laporan set id_suratkeluar=? where id_suratmasuk=?",
                "data berhasil diubah", idKeluar, idMasuk);
    }

    public void hapusLaporan(String idMasuk) {
        jalankan("delete from tabel_laporan where id_suratmasuk=?",
                "data berhasil dihapus", idMasuk);
    }
}