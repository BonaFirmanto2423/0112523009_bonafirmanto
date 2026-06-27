import express from "express";
import cors from "cors";
import path from "path";

import mahasiswaRoutes from "./routes/mahasiswa.route";
import prodiRoutes from "./routes/prodi.route";

const app = express();

// Middleware CORS

app.use(
  cors({
    origin: "http://localhost:3001",
    credentials: true,
    methods: [
      "GET",
      "POST",
      "PUT",
      "DELETE",
      "OPTIONS",
    ],
    allowedHeaders: [
      "Content-Type",
      "Authorization",
    ],
  })
);

// Middleware JSON
app.use(express.json());

// Folder Upload
app.use(
  "/uploads",
  express.static(path.join(process.cwd(), "uploads"))
);

// Route Testing
app.get("/", (req, res) => {
  res.json({
    message: "Backend Express berjalan",
  });
});

// Route Mahasiswa
app.use("/api/mahasiswa", mahasiswaRoutes);

// Route Prodi
app.use("/api/prodi", prodiRoutes);

export default app;