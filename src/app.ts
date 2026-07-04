import express from "express";
import cors from "cors";
import mahasiswaRoutes from "./routes/mahasiswa.route";

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.json({
    message: "API Express CRUD berjalan",
  });
});

app.use("/api/mahasiswa", mahasiswaRoutes);

export default app;