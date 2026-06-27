export function saveAuth(
  token: string,
  user: any
) {

  localStorage.setItem(
    "token",
    token
  );

  localStorage.setItem(
    "user",
    JSON.stringify(user)
  );
}

export function getToken() {

  if (typeof window === "undefined") {
    return null;
  }

  return localStorage.getItem(
    "token"
  );
}

export function getUser() {

  if (typeof window === "undefined") {
    return null;
  }

  const raw =
    localStorage.getItem("user");

  return raw
    ? JSON.parse(raw)
    : null;
}

export function logout() {

  if (typeof window !== "undefined") {

    localStorage.removeItem(
      "token"
    );

    localStorage.removeItem(
      "user"
    );

    window.location.href =
      "/login";
  }
}

export function isAuthenticated() {

  if (typeof window === "undefined") {
    return false;
  }

  return !!localStorage.getItem(
    "token"
  );
}