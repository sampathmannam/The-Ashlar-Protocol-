import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../theme/ashlar_colors.dart';

class CrisisSupport {
  static final CrisisSupport instance = CrisisSupport._();
  CrisisSupport._();

  static const _hotlines = [
    CrisisLine(
      name: '988 Suicide & Crisis Lifeline',
      number: '988',
      description: 'Call or text 24/7',
    ),
    CrisisLine(
      name: 'Crisis Text Line',
      number: '741741',
      description: 'Text HOME to 741741',
    ),
    CrisisLine(
      name: 'International Association for Suicide Prevention',
      number: '',
      description: 'https://www.iasp.info/resources/Crisis_Centres/',
    ),
  ];

  List<CrisisLine> get hotlines => _hotlines;

  Future<void> call(String number) async {
    final uri = Uri(scheme: 'tel', path: number);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri);
    }
  }

  Future<void> openWebsite(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    }
  }
}

class CrisisLine {
  final String name;
  final String number;
  final String description;

  const CrisisLine({
    required this.name,
    required this.number,
    required this.description,
  });
}

class CrisisSupportDialog extends StatelessWidget {
  const CrisisSupportDialog({super.key});

  static void show(BuildContext context) {
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (_) => const CrisisSupportDialog(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      backgroundColor: AshlarColors.surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(
          color: AshlarColors.redAlert.withAlpha(100),
        ),
      ),
      title: Row(
        children: [
          Icon(
            Icons.emergency,
            color: AshlarColors.redAlert,
            size: 24,
          ),
          const SizedBox(width: 12),
          Text(
            'Need Help Now?',
            style: TextStyle(color: AshlarColors.lightText),
          ),
        ],
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'You are not alone. Real people are ready to help.',
            style: TextStyle(
              color: AshlarColors.silver,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 20),
          ...CrisisSupport.instance.hotlines.map(
            (line) => Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: InkWell(
                onTap: () {
                  if (line.number.isNotEmpty) {
                    CrisisSupport.instance.call(line.number);
                  } else {
                    CrisisSupport.instance.openWebsite(line.description);
                  }
                },
                borderRadius: BorderRadius.circular(8),
                child: Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AshlarColors.charcoal,
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(
                      color: AshlarColors.dividerWhite.withAlpha(80),
                    ),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              line.name,
                              style: TextStyle(
                                color: AshlarColors.lightText,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              line.description,
                              style: TextStyle(
                                color: AshlarColors.mutedText,
                                fontSize: 12,
                              ),
                            ),
                          ],
                        ),
                      ),
                      if (line.number.isNotEmpty)
                        Text(
                          line.number,
                          style: TextStyle(
                            color: AshlarColors.gold,
                            fontSize: 18,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: Text(
            'Close',
            style: TextStyle(color: AshlarColors.silver),
          ),
        ),
      ],
    );
  }
}