import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../features/initiation/initiation_screen.dart';
import '../../features/board/board_screen.dart';
import '../../features/tools/chamber_screen.dart';
import '../../features/tools/tools_screen.dart';
import '../crisis/crisis_support.dart';
import '../theme/ashlar_colors.dart';

final _rootNavigatorKey = GlobalKey<NavigatorState>();
final _shellNavigatorKey = GlobalKey<NavigatorState>();

final appRouter = GoRouter(
  navigatorKey: _rootNavigatorKey,
  initialLocation: '/initiation',
  routes: [
    GoRoute(
      path: '/initiation',
      builder: (context, state) => const InitiationScreen(),
    ),
    ShellRoute(
      navigatorKey: _shellNavigatorKey,
      builder: (context, state, child) => ScaffoldWithNav(child: child),
      routes: [
        GoRoute(
          path: '/board',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: BoardScreen(),
          ),
        ),
        GoRoute(
          path: '/chamber',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: ChamberScreen(),
          ),
        ),
        GoRoute(
          path: '/tools',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: ToolsScreen(),
          ),
        ),
      ],
    ),
  ],
);

class ScaffoldWithNav extends StatelessWidget {
  final Widget child;
  const ScaffoldWithNav({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final currentPath = GoRouterState.of(context).uri.path;

    return Scaffold(
      backgroundColor: AshlarColors.charcoal,
      body: Column(
        children: [
          _Header(onNeedHelp: () => CrisisSupportDialog.show(context)),
          Expanded(child: child),
        ],
      ),
      bottomNavigationBar: _BottomNav(currentPath: currentPath),
    );
  }
}

class _Header extends StatelessWidget {
  final VoidCallback onNeedHelp;
  const _Header({required this.onNeedHelp});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      bottom: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(24, 16, 24, 8),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'THE WORK',
                  style: Theme.of(context).textTheme.labelSmall,
                ),
                const SizedBox(height: 4),
                Text(
                  'THE ASHLAR',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            GestureDetector(
              onTap: onNeedHelp,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                decoration: BoxDecoration(
                  color: AshlarColors.redAlertSoft,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(
                    color: AshlarColors.redAlert.withAlpha(120),
                  ),
                ),
                child: Text(
                  'NEED HELP?',
                  style: Theme.of(context).textTheme.labelSmall?.copyWith(
                    color: AshlarColors.redAlert,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _BottomNav extends StatelessWidget {
  final String currentPath;
  const _BottomNav({required this.currentPath});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(24, 0, 24, 32),
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 24),
      decoration: BoxDecoration(
        color: AshlarColors.surface.withAlpha(240),
        borderRadius: BorderRadius.circular(32),
        border: Border.all(
          color: AshlarColors.dividerWhite.withAlpha(30),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _NavItem(
            icon: Icons.home_outlined,
            label: 'BOARD',
            isSelected: currentPath == '/board',
            onTap: () => context.go('/board'),
          ),
          _NavItem(
            icon: Icons.delete_outline,
            label: 'CHAMBER',
            isSelected: currentPath == '/chamber',
            onTap: () => context.go('/chamber'),
          ),
          _NavItem(
            icon: Icons.build_outlined,
            label: 'TOOLS',
            isSelected: currentPath == '/tools',
            onTap: () => context.go('/tools'),
          ),
        ],
      ),
    );
  }
}

class _NavItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  const _NavItem({
    required this.icon,
    required this.label,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 48,
            height: 32,
            decoration: BoxDecoration(
              color: isSelected
                  ? AshlarColors.gold.withAlpha(50)
                  : Colors.transparent,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(
              icon,
              color: isSelected ? AshlarColors.gold : AshlarColors.silver,
              size: 20,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
              color: isSelected ? AshlarColors.gold : AshlarColors.silver,
              fontSize: 9,
            ),
          ),
        ],
      ),
    );
  }
}