import { Request, Response } from "express";
import pool from "../config/db";

export const getAllProdi = async (
  req: Request,
  res: Response
) => {
  try {
    const [rows] = await pool.query(
      "SELECT * FROM prodi ORDER BY nama_prodi ASC"
    );

    res.status(200).json({
      message: "Data prodi berhasil diambil",
      data: rows,
    });
  } catch (error: any) {
    console.error(error);

    res.status(500).json({
      message: "Gagal mengambil data prodi",
      error: error.message,
    });
  }
};