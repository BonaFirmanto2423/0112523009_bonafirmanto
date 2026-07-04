import { Request, Response } from "express";
import pool from "../config/db";
import bcrypt from "bcrypt";
import jwt, { Secret, SignOptions } from "jsonwebtoken";

export const register = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      res.status(400).json({
        success: false,
        message: "Semua field wajib diisi",
      });
      return;
    }

    const [rows]: any = await pool.query(
      "SELECT id FROM users WHERE email = ?",
      [email]
    );

    if (rows.length > 0) {
      res.status(400).json({
        success: false,
        message: "Email sudah digunakan",
      });
      return;
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    await pool.query(
      `
      INSERT INTO users
      (name,email,password,role)
      VALUES (?,?,?,?)
      `,
      [
        name,
        email,
        hashedPassword,
        "viewer",
      ]
    );

    res.status(201).json({
      success: true,
      message: "Registrasi berhasil",
    });

  } catch (error: any) {

    console.error(error);

    res.status(500).json({
      success: false,
      message: "Server Error",
      error: error.message,
    });

  }
};

export const login = async (
  req: Request,
  res: Response
): Promise<void> => {

  try {

    const { email, password } = req.body;

    if (!email || !password) {
      res.status(400).json({
        success: false,
        message: "Email dan password wajib diisi",
      });
      return;
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
      res.status(401).json({
        success: false,
        message: "Email atau password salah",
      });
      return;
    }

    const user = rows[0];

    const validPassword = await bcrypt.compare(
      password,
      user.password
    );

    if (!validPassword) {
      res.status(401).json({
        success: false,
        message: "Email atau password salah",
      });
      return;
    }

    const secret: Secret =
      process.env.JWT_SECRET || "mysecret";

    const options: SignOptions = {
      expiresIn: "2h",
    };

    const token = jwt.sign(
      {
        id: user.id,
        email: user.email,
        role: user.role,
      },
      secret,
      options
    );

    res.status(200).json({
      success: true,
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
      success: false,
      message: "Server Error",
      error: error.message,
    });

  }

};