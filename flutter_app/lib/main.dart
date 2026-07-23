import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/ashlar_theme.dart';
import 'core/router/app_router.dart';
import 'core/persistence/local_storage.dart';
import 'features/initiation/initiation_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final storage = await LocalStorage.getInstance();

  runApp(
    ProviderScope(
      overrides: [
        localStorageProvider.overrideWithValue(storage),
      ],
      child: const AshlarApp(),
    ),
  );
}

class AshlarApp extends StatelessWidget {
  const AshlarApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'The Ashlar Protocol',
      theme: AshlarTheme.dark,
      routerConfig: appRouter,
      debugShowCheckedModeBanner: false,
    );
  }
}