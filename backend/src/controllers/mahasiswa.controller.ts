import { Request, Response } from "express";
import pool from "../config/db";
import fs from "fs";
import path from "path";

export const getAllMahasiswa = async (
  req: Request,
  res: Response
) => {
  try {
    const [rows] = await pool.query(`
      SELECT
        m.id,
        m.nim,
        m.nama,
        m.prodi_id,
        p.nama_prodi,
        m.angkatan,
        m.foto,
        m.created_at,
        m.updated_at
      FROM mahasiswa m
      LEFT JOIN prodi p
      ON m.prodi_id = p.id
      ORDER BY m.id DESC
    `);

    res.status(200).json({
      message: "Data mahasiswa berhasil diambil",
      data: rows,
    });
  } catch (error: any) {
    console.error(error);

    res.status(500).json({
      message: "Gagal mengambil data",
      error: error.message,
    });
  }
};

export const createMahasiswa = async (
  req: Request,
  res: Response
) => {

  console.log("===== BODY =====");
  console.log(req.body);

  console.log("===== FILE =====");
  console.log(req.file);

  try {
    const { nim, nama, prodi_id, angkatan } = req.body;
    const foto = req.file ? req.file.filename : null;

    const [result]: any = await pool.query(
      `
      INSERT INTO mahasiswa
      (
        nim,
        nama,
        prodi_id,
        angkatan,
        foto
      )
      VALUES
      (?,?,?,?,?)
      `,
      [
        nim,
        nama,
        prodi_id,
        angkatan,
        foto,
      ]
    );

    res.status(201).json({
      message: "Data mahasiswa berhasil ditambahkan",
      data: {
        id: result.insertId,
        nim,
        nama,
        prodi_id,
        angkatan,
        foto,
      },
    });
  } catch (error: any) {
    console.error(error);

    res.status(500).json({
      message: "Gagal menambah data",
      error: error.message,
    });
  }
};

export const updateMahasiswa = async (
  req: Request,
  res: Response
) => {
  try {
    const { id } = req.params;
    const { nim, nama, prodi_id, angkatan } = req.body;

    // Ambil foto lama
    const [rows]: any = await pool.query(
      "SELECT foto FROM mahasiswa WHERE id=?",
      [id]
    );

    if (rows.length === 0) {
      return res.status(404).json({
        message: "Mahasiswa tidak ditemukan",
      });
    }

    let foto = rows[0].foto;

    // Jika upload foto baru
    if (req.file) {
      // Hapus foto lama
      if (foto) {
        const oldPath = path.join(
          process.cwd(),
          "uploads",
          "mahasiswa",
          foto
        );

        if (fs.existsSync(oldPath)) {
          fs.unlinkSync(oldPath);
        }
      }

      foto = req.file.filename;
    }

    await pool.query(
      `
      UPDATE mahasiswa
      SET
        nim=?,
        nama=?,
        prodi_id=?,
        angkatan=?,
        foto=?
      WHERE id=?
      `,
      [
        nim,
        nama,
        prodi_id,
        angkatan,
        foto,
        id,
      ]
    );

    res.status(200).json({
      message: "Data mahasiswa berhasil diperbarui",
      data: {
        id,
        nim,
        nama,
        prodi_id,
        angkatan,
        foto,
      },
    });

  } catch (error: any) {
    console.error(error);

    res.status(500).json({
      message: "Gagal update data",
      error: error.message,
    });
  }
};

export const deleteMahasiswa = async (
  req: Request,
  res: Response
) => {
  try {

    const { id } = req.params;

    // Ambil nama foto
    const [rows]: any = await pool.query(
      "SELECT foto FROM mahasiswa WHERE id=?",
      [id]
    );

    if (rows.length === 0) {
      return res.status(404).json({
        message: "Mahasiswa tidak ditemukan",
      });
    }

    const foto = rows[0].foto;

    // Hapus file foto
    if (foto) {
      const filePath = path.join(
        process.cwd(),
        "uploads",
        "mahasiswa",
        foto
      );

      if (fs.existsSync(filePath)) {
        fs.unlinkSync(filePath);
      }
    }

    // Hapus data database
    await pool.query(
      "DELETE FROM mahasiswa WHERE id=?",
      [id]
    );

    res.status(200).json({
      message: "Data mahasiswa berhasil dihapus",
    });

  } catch (error: any) {

    console.error(error);

    res.status(500).json({
      message: "Gagal menghapus data",
      error: error.message,
    });

  }
};