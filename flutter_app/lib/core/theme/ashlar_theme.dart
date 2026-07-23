import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'ashlar_colors.dart';

class AshlarTheme {
  AshlarTheme._();

  static ThemeData get dark {
    final baseTextTheme = GoogleFonts.interTextTheme(
      ThemeData.dark().textTheme,
    );

    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      scaffoldBackgroundColor: AshlarColors.charcoal,
      colorScheme: const ColorScheme.dark(
        surface: AshlarColors.surface,
        primary: AshlarColors.gold,
        secondary: AshlarColors.silver,
        error: AshlarColors.redAlert,
      ),
      textTheme: baseTextTheme.copyWith(
        headlineLarge: baseTextTheme.headlineLarge?.copyWith(
          color: AshlarColors.lightText,
          fontWeight: FontWeight.w300,
          letterSpacing: 2.0,
        ),
        headlineMedium: baseTextTheme.headlineMedium?.copyWith(
          color: AshlarColors.lightText,
          fontWeight: FontWeight.w300,
        ),
        titleLarge: baseTextTheme.titleLarge?.copyWith(
          color: AshlarColors.lightText,
          fontWeight: FontWeight.w400,
          letterSpacing: 1.5,
        ),
        titleMedium: baseTextTheme.titleMedium?.copyWith(
          color: AshlarColors.lightText,
        ),
        bodyLarge: baseTextTheme.bodyLarge?.copyWith(
          color: AshlarColors.lightText,
          height: 1.6,
        ),
        bodyMedium: baseTextTheme.bodyMedium?.copyWith(
          color: AshlarColors.silver,
          height: 1.5,
        ),
        bodySmall: baseTextTheme.bodySmall?.copyWith(
          color: AshlarColors.mutedText,
        ),
        labelLarge: baseTextTheme.labelLarge?.copyWith(
          color: AshlarColors.gold,
          letterSpacing: 2.0,
          fontWeight: FontWeight.w500,
        ),
        labelSmall: baseTextTheme.labelSmall?.copyWith(
          color: AshlarColors.gold,
          letterSpacing: 2.0,
        ),
      ),
    );
  }
}