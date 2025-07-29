function signal = apply_reverb(signal, impulse_response)
  % Change stereo impulse response to mono
  impulse_response = stereo_to_mono(impulse_response);

  % Compute the convolution
  signal = fftconv(signal, impulse_response);

  % Normalize
  signal = signal / norm(signal, inf);
end
