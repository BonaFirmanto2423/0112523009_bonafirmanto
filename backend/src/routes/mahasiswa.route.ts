import { Router } from "express";

import {
  getAllMahasiswa,
  createMahasiswa,
  updateMahasiswa,
  deleteMahasiswa,
} from "../controllers/mahasiswa.controller";

import upload from "../middlewares/upload.middleware";
import { verifyToken } from "../middlewares/auth.middleware";
import { allowRoles } from "../middlewares/role.middleware";

const router = Router();

/**
 * =====================================
 * MAHASISWA ROUTES
 * Base URL : /api/mahasiswa
 * =====================================
 */

// Ambil semua data mahasiswa
router.get(
  "/",
  verifyToken,
  getAllMahasiswa
);

// Tambah mahasiswa
router.post(
  "/",
  verifyToken,
  upload.single("foto"),
  createMahasiswa
);

// Update mahasiswa
router.put(
  "/:id",
  verifyToken,
  upload.single("foto"),
  updateMahasiswa
);

// Hapus mahasiswa
router.delete(
  "/:id",
  verifyToken,
  deleteMahasiswa
);

export default router;  