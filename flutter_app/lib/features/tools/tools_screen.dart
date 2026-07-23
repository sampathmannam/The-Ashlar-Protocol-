import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/ashlar_colors.dart';
import '../stone/degrees.dart';
import 'tools_provider.dart';
import 'plumb/plumb_screen.dart';
import 'gauge/gauge_screen.dart';
import 'breath_pacer/breath_pacer_screen.dart';
import 'daily_word/daily_word_screen.dart';

class ToolsScreen extends ConsumerWidget {
  const ToolsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final toolsState = ref.watch(toolsProvider);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'THE TOOLS',
            style: Theme.of(context).textTheme.labelLarge?.copyWith(
              letterSpacing: 2,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Each tool is given as you are ready for it.',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 24),

          // Apprentice tools
          _ToolSection(
            title: 'APPRENTICE TOOLS',
            tools: [
              _ToolItem(
                name: 'The Gauge',
                description: 'Behavioral activation — one small act of order.',
                icon: Icons.straighten,
                isAvailable: true,
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const GaugeScreen()),
                ),
              ),
              _ToolItem(
                name: 'The Gavel',
                description: 'Notice and interrupt a pattern.',
                icon: Icons.touch_app_outlined,
                isAvailable: true,
                onTap: () {},
              ),
              _ToolItem(
                name: 'The Level',
                description: 'Paced breathing — find your rhythm.',
                icon: Icons.waves,
                isAvailable: true,
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const BreathPacerScreen()),
                ),
              ),
            ],
          ),
          const SizedBox(height: 24),

          // Fellowcraft tools
          _ToolSection(
            title: 'FELLOWCRAFT TOOLS',
            tools: [
              _ToolItem(
                name: 'The Plumb',
                description: 'CBT thought-record — straighten a leaning thought.',
                icon: Icons.vertical_align_center,
                isAvailable: toolsState.degree.index >= AshlarDegree.fellowcraft.index,
                onTap: toolsState.degree.index >= AshlarDegree.fellowcraft.index
                    ? () => Navigator.push(
                          context,
                          MaterialPageRoute(builder: (_) => const PlumbScreen()),
                        )
                    : null,
              ),
              _ToolItem(
                name: 'Mouth to Ear',
                description: 'Memorize a principle that matters to you.',
                icon: Icons.record_voice_over,
                isAvailable: toolsState.degree.index >= AshlarDegree.fellowcraft.index,
                onTap: null,
              ),
            ],
          ),
          const SizedBox(height: 24),

          // Master Mason tools
          _ToolSection(
            title: 'MASTER MASON TOOLS',
            tools: [
              _ToolItem(
                name: 'Relief',
                description: 'Turn outward — reach toward another.',
                icon: Icons.handshake,
                isAvailable: toolsState.degree.index >= AshlarDegree.masterMason.index,
                onTap: null,
              ),
              _ToolItem(
                name: 'Reach Out',
                description: 'Contact someone who matters.',
                icon: Icons.phone_outlined,
                isAvailable: toolsState.degree.index >= AshlarDegree.masterMason.index,
                onTap: null,
              ),
            ],
          ),
          const SizedBox(height: 24),

          // Daily word
          const DailyWordCard(),
        ],
      ),
    );
  }
}

class _ToolSection extends StatelessWidget {
  final String title;
  final List<_ToolItem> tools;

  const _ToolSection({
    required this.title,
    required this.tools,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: Theme.of(context).textTheme.labelSmall?.copyWith(
            letterSpacing: 1,
          ),
        ),
        const SizedBox(height: 12),
        ...tools.map(
          (tool) => Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: _ToolCard(tool: tool),
          ),
        ),
      ],
    );
  }
}

class _ToolItem {
  final String name;
  final String description;
  final IconData icon;
  final bool isAvailable;
  final VoidCallback? onTap;

  const _ToolItem({
    required this.name,
    required this.description,
    required this.icon,
    required this.isAvailable,
    this.onTap,
  });
}

class _ToolCard extends StatelessWidget {
  final _ToolItem tool;

  const _ToolCard({required this.tool});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: tool.isAvailable ? tool.onTap : null,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AshlarColors.surface,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: tool.isAvailable
                ? AshlarColors.dividerWhite.withAlpha(60)
                : AshlarColors.dividerWhite.withAlpha(30),
          ),
        ),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: tool.isAvailable
                    ? AshlarColors.gold.withAlpha(30)
                    : AshlarColors.slate.withAlpha(50),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Icon(
                tool.icon,
                color: tool.isAvailable
                    ? AshlarColors.gold
                    : AshlarColors.mutedText,
                size: 20,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    tool.name,
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: tool.isAvailable
                          ? AshlarColors.lightText
                          : AshlarColors.mutedText,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    tool.isAvailable
                        ? tool.description
                        : 'This tool awaits you.',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: tool.isAvailable
                          ? AshlarColors.silver
                          : AshlarColors.mutedText,
                    ),
                  ),
                ],
              ),
            ),
            if (!tool.isAvailable)
              Icon(
                Icons.lock_outline,
                color: AshlarColors.mutedText,
                size: 16,
              ),
          ],
        ),
      ),
    );
  }
}