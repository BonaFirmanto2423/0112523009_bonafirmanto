import { Request, Response } from "express";
import pool from "../config/db";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

export const register = async (
  req: Request,
  res: Response
) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({
        message: "Semua field wajib diisi",
      });
    }

    if (password.length < 6) {
      return res.status(400).json({
        message: "Password minimal 6 karakter",
      });
    }

    const [rows]: any = await pool.query(
      "SELECT id FROM users WHERE email = ?",
      [email]
    );

    if (rows.length > 0) {
      return res.status(400).json({
        message: "Email sudah digunakan",
      });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    await pool.query(
      `
      INSERT INTO users
      (name, email, password, role)
      VALUES
      (?, ?, ?, ?)
      `,
      [
        name,
        email,
        hashedPassword,
        "viewer",
      ]
    );

    res.status(201).json({
      message: "Registrasi berhasil",
    });

  } catch (error: any) {

    console.error(error);

    res.status(500).json({
      message: "Terjadi kesalahan server",
      error: error.message,
    });

  }
};

export const login = async (
  req: Request,
  res: Response
) => {
  try {

    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        message: "Email dan password wajib diisi",
      });
    }

    const [rows]: any = await pool.query(
      `
      SELECT
      id,
      name,
      email,
      password,
      role
      FROM users
      WHERE email = ?
      `,
      [email]
    );

    if (rows.length === 0) {
      return res.status(401).json({
        message: "Email atau password salah",
      });
    }

    const user = rows[0];

    const match = await bcrypt.compare(
      password,
      user.password
    );

    if (!match) {
      return res.status(401).json({
        message: "Email atau password salah",
      });
    }

   const secret = process.env.JWT_SECRET || "mysecret";

const token = jwt.sign(
  {
    id: user.id,
    email: user.email,
    role: user.role,
  },
  secret,
  {
    expiresIn: "2h",
  } as jwt.SignOptions
);

    res.status(200).json({
      message: "Login berhasil",
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role,
      },
    });

  } catch (error: any) {

    console.error(error);

    res.status(500).json({
      message: "Terjadi kesalahan server",
      error: error.message,
    });

  }
};