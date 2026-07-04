import { Router, Request, Response } from "express";
import { register, login } from "../controllers/auth.controller";

const router = Router();

/**
 * ======================================
 * AUTH ROUTES
 * Base URL : /api/auth
 * ======================================
 */

// Test Route
router.get("/", (req: Request, res: Response) => {
  res.status(200).json({
    success: true,
    message: "Authentication API berjalan",
  });
});

// Register User
router.post("/register", register);

// Login User
router.post("/login", login);

// Logout
router.post("/logout", (req: Request, res: Response) => {
  res.status(200).json({
    success: true,
    message: "Logout berhasil",
  });
});

export default router;